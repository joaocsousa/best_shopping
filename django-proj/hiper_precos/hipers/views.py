from hipers.models import Hiper, Categoria, Produto
from hipers.serializers import HiperSerializer, CategoriaSerializer, ProdutoSerializer
from rest_framework import generics, permissions, renderers
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse
from django.http.response import HttpResponse

from hiper_precos.utils import Utils

class HiperList(generics.ListCreateAPIView):
    model = Hiper
    serializer_class = HiperSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class HiperDetail(generics.RetrieveUpdateDestroyAPIView):
    model = Hiper
    serializer_class = HiperSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class CategoriaList(generics.ListCreateAPIView):
    model = Categoria
    serializer_class = CategoriaSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def get_queryset(self):
        queryset = Categoria.objects.all()
        hiper = self.request.QUERY_PARAMS.get('hiper', None)
        if hiper is not None:
            queryset = queryset.filter(hiper=hiper)
        categoria_pai = self.request.QUERY_PARAMS.get('categoria_pai', None)
        if categoria_pai is not None:
            if categoria_pai == "-1":
                queryset = queryset.exclude(categoria_pai__isnull=False)
            else:
                queryset = queryset.filter(categoria_pai=categoria_pai)
        return queryset

class CategoriaDetail(generics.RetrieveUpdateDestroyAPIView):
    model = Categoria
    serializer_class = CategoriaSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class ProdutoList(generics.ListCreateAPIView):
    model = Produto
    serializer_class = ProdutoSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def get_queryset(self):
        queryset = Produto.objects.all()
        hiper = self.request.QUERY_PARAMS.get('hiper', None)
        if hiper is not None:
            queryset = queryset.filter(hiper=hiper)
        categoria = self.request.QUERY_PARAMS.get('categoria', None)
        if categoria is not None:
            queryset = queryset.filter(categoria_pai=categoria)
        desconto = self.request.QUERY_PARAMS.get('desconto', None)
        if desconto is not None:
            if desconto == "1":
                queryset = queryset.exclude(desconto__isnull=True)
            elif desconto == "0":
                queryset = queryset.exclude(desconto__isnull=False)
        return queryset

class ProdutoDetail(generics.RetrieveUpdateDestroyAPIView):
    model = Produto
    serializer_class = ProdutoSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

@api_view(('GET',))
def api_root(request, format=None):
    return Response({
            'hipers': reverse('hiper-list', request=request, format=format),
            'categorias': reverse('categoria-list', request=request, format=format),
            'produtos': reverse('produto-list', request=request, format=format)
        })

# this method has the purpose of informing the server
# that the database was updated to update the log file
def hipers_updated(request, format=None):
    try:
        Utils.dbPopulated()
        return HttpResponse("OK");
    except:
        pass
    return HttpResponse("FAILED");