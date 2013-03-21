import datetime
from haystack import indexes
from models import Categoria, Produto


class CategoriaIndex(indexes.SearchIndex, indexes.Indexable):
    text = indexes.CharField(document=True, use_template=True)
    hiper = indexes.CharField(model_attr='hiper')

    def get_model(self):
        return Categoria

    def index_queryset(self, using=None):
        """Used when the entire index for model is updated."""
        return self.get_model().objects.all()

class ProdutoIndex(indexes.SearchIndex, indexes.Indexable):
    text = indexes.CharField(document=True, use_template=True)
    nome = indexes.CharField(model_attr='nome')
    marca = indexes.CharField(model_attr='marca')
    preco = indexes.CharField(model_attr='preco')

    def get_model(self):
        return Produto

    def index_queryset(self, using=None):
        """Used when the entire index for model is updated."""
        return self.get_model().objects.all()