# -*- coding: utf-8 -*-
from django.db import models
import hiper_precos.utils

# Create your models here.
class Hiper(models.Model):
    nome          = models.CharField(max_length=100)
    domain        = models.CharField(max_length=300)
    mainPath      = models.CharField(max_length=300)
    latest_update = models.DateTimeField()

    def __unicode__(self):
        return self.nome

    def get_latest_update(self):
        return hiper_precos.utils.Utils.toUTC(self.latest_update)

class Categoria(models.Model):
    url           = models.CharField(max_length=300, null=True)
    nome          = models.CharField(max_length=100)
    hiper         = models.ForeignKey(Hiper, related_name='categorias')
    categoria_pai = models.ForeignKey('self', null=True, related_name='sub_categorias')
    latest_update = models.DateTimeField()

    def __unicode__(self):
        catPai = None
        try:
            catPai = self.categoria_pai.id
        except:
            pass
        return {
                    "id"           : self.id,
                    "nome"         : self.nome,
                    "hiper"        : self.hiper.id,
                    "categoria_pai": catPai,
                    "latest_update": hiper_precos.utils.Utils.toUTC(self.latest_update)
                }

    def get_latest_update(self):
        return hiper_precos.utils.Utils.toUTC(self.latest_update)

    # def _recurse_for_parents(self, cat_obj):
    #     p_list = []
    #     if cat_obj.categoria_pai_id:
    #         p = cat_obj.categoria_pai
    #         p_list.append(p.nome)
    #         more = self._recurse_for_parents(p)
    #         p_list.extend(more)
    #     if cat_obj == self and p_list:
    #         p_list.reverse()
    #     return p_list

    # def get_separator(self):
    #     return ' :: '

class Produto(models.Model):
    nome          = models.CharField(max_length=300, null=True)
    marca         = models.CharField(max_length=100, null=True)
    preco         = models.FloatField(default=None, null=True)
    preco_kg      = models.FloatField(default=None, null=True)
    peso          = models.CharField(max_length=200, null=True)
    url_pagina    = models.CharField(max_length=300, null=True)
    url_imagem    = models.CharField(max_length=300, null=True)
    desconto      = models.FloatField(default=None, null=True)
    categoria_pai = models.ForeignKey(Categoria, related_name='produtos')
    hiper         = models.ForeignKey(Hiper, related_name='produtos')
    latest_update = models.DateTimeField()

    def __unicode__(self):
        return {
                    "id"           : self.id,
                    "nome"         : self.nome,
                    "marca"        : self.marca,
                    "preco"        : self.preco,
                    "preco_kg"     : self.preco_kg,
                    "peso"         : self.peso,
                    "url_pagina"   : self.url_pagina,
                    "url_imagem"   : self.url_imagem,
                    "desconto"     : self.desconto,
                    "categoria_pai": self.categoria_pai.id,
                    "hiper"        : self.hiper.id,
                    "latest_update": hiper_precos.utils.Utils.toUTC(self.latest_update)
                }

    def get_latest_update(self):
        return hiper_precos.utils.Utils.toUTC(self.latest_update)
