# -*- coding: utf-8 -*-
from rest_framework import serializers
from hipers.models import Hiper, Categoria, Produto

class HiperSerializer(serializers.ModelSerializer):
    categorias = serializers.RelatedField(many=True)
    class Meta:
        model = Hiper
        fields = ('id', 'nome', 'domain', 'mainPath', 'categorias')

class CategoriaSerializer(serializers.ModelSerializer):
    produtos = serializers.RelatedField(many=True)
    sub_categorias = serializers.RelatedField(many=True)
    class Meta:
        model = Categoria
        fields = ('id', 'url', 'nome', 'slug', 'hiper', 'categoria_pai', 'sub_categorias', 'produtos')

class ProdutoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Produto
        fields = ('id', 'nome', 'marca', 'preco', 'preco_kg', 'peso', 'url_pagina', 'url_imagem', 'desconto', 'categoria_pai', 'last_updated')