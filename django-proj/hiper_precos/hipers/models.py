# -*- coding: utf-8 -*-
from django.db import models
from django.core import validators
from django.template.defaultfilters import slugify

# Create your models here.
class Hiper(models.Model):
    nome = models.CharField(max_length=100)
    domain = models.CharField(max_length=300)
    mainPath = models.CharField(max_length=300)

    def __unicode__(self):
        return self.nome
        
class Categoria(models.Model):
    url = models.CharField(max_length=300, null=True)
    nome = models.CharField(max_length=100)
    slug = models.SlugField()
    categoria_pai = models.ForeignKey('self', null=True, related_name='sub_categorias')
    hiper = models.ForeignKey(Hiper, related_name='categorias')

    def __unicode__(self):
        p_list = self._recurse_for_parents(self)

        p_list.append(self.nome)
        return self.get_separator().join(p_list)

    def get_absolute_url(self):
        if self.categoria_pai_id:
            return "/categorias/%s/%s/" % (self.categoria_pai.slug, self.slug)
        else:
            return "/categorias/%s/" % (self.id)

    def _recurse_for_parents(self, cat_obj):
        p_list = []
        if cat_obj.categoria_pai_id:
            p = cat_obj.categoria_pai
            p_list.append(p.nome)
            more = self._recurse_for_parents(p)
            p_list.extend(more)
        if cat_obj == self and p_list:
            p_list.reverse()
        return p_list

    def get_separator(self):
        return ' :: '

    def _parents_repr(self):
        p_list = self._recurse_for_parents(self)
        return self.get_separator().join(p_list)

    def save(self, *args, **kwargs):
        if not self.id:
            # Newly created object, so set slug
            self.slug = slugify(self.nome)
        super(Categoria, self).save()

class Produto(models.Model):
    nome = models.CharField(max_length=200, null=True)
    marca = models.CharField(max_length=100, null=True)
    preco = models.FloatField(default=None, null=True)
    preco_kg = models.FloatField(default=None, null=True)
    peso = models.CharField(max_length=200, null=True)
    url_pagina = models.CharField(max_length=300, null=True)
    url_imagem = models.CharField(max_length=300, null=True)
    desconto = models.FloatField(default=None, null=True)
    categoria_pai = models.ForeignKey(Categoria, related_name='produtos')
    last_updated = models.DateTimeField()

    def __unicode__(self):
        return u'%s - %s - %s' % (unicode(self.categoria_pai), self.nome, self.marca)