# -*- coding: utf-8 -*-
from rest_framework import serializers
from hipers.models import Hiper, Categoria, Produto

class HiperSerializer(serializers.ModelSerializer):
    categorias = serializers.RelatedField(many=True)
    latestUpdate = serializers.Field(source='get_latest_update')
    class Meta:
        model = Hiper
        fields = ('id', 'nome', 'domain', 'mainPath', 'categorias', 'latestUpdate')

class HiperResultSerializer(serializers.ModelSerializer):
    latestUpdate = serializers.Field(source='get_latest_update')
    class Meta:
        model = Hiper
        fields = ('id', 'nome', 'domain', 'mainPath', 'latestUpdate')

class CategoriaSerializer(serializers.ModelSerializer):
    produtos = serializers.RelatedField(many=True)
    sub_categorias = serializers.RelatedField(many=True)
    latestUpdate = serializers.Field(source='get_latest_update')
    class Meta:
        model = Categoria
        fields = ('id', 'nome', 'hiper', 'categoria_pai', 'sub_categorias', 'produtos', 'latestUpdate')

class CategoriaListSerializer(serializers.ModelSerializer):
    latestUpdate = serializers.Field(source='get_latest_update')
    class Meta:
        model = Categoria
        fields = ('id', 'nome', 'hiper', 'categoria_pai', 'latestUpdate')

class ProdutoSerializer(serializers.ModelSerializer):
    latestUpdate = serializers.Field(source='get_latest_update')
    class Meta:
        model = Produto
        fields = ("id", "nome", "marca", "preco", "preco_kg", "peso", "url_pagina", "url_imagem", "desconto", "categoria_pai", "hiper", "latestUpdate")
