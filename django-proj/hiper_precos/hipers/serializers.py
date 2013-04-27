# -*- coding: utf-8 -*-
from rest_framework import serializers
from hipers.models import Hiper, Categoria, Produto

class HiperSerializer(serializers.ModelSerializer):
    categorias = serializers.RelatedField(many=True)
    class Meta:
        model = Hiper
        fields = ('id', 'nome', 'slug', 'domain', 'mainPath', 'categorias')

class HiperResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = Hiper
        fields = ('id', 'nome')

class CategoriaSerializer(serializers.ModelSerializer):
    produtos = serializers.RelatedField(many=True)
    sub_categorias = serializers.RelatedField(many=True)
    class Meta:
        model = Categoria
        fields = ('id', 'nome', 'hiper', 'categoria_pai', 'sub_categorias', 'produtos')

class CategoriaListSerializer(serializers.ModelSerializer):
    produtos = serializers.RelatedField(many=True)
    sub_categorias = serializers.RelatedField(many=True)
    class Meta:
        model = Categoria
        fields = ('id', 'nome', 'hiper', 'categoria_pai')

class ProdutoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Produto
        fields = ('id', 'nome', 'marca', 'preco', 'preco_kg', 'peso', 'url_pagina', 'url_imagem', 'desconto', 'last_updated')
