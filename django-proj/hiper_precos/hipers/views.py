from hipers.models import Hiper, Categoria, Produto
from hipers.serializers import HiperSerializer, CategoriaSerializer, ProdutoSerializer
from rest_framework import generics, permissions, renderers
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse

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

class CategoriaDetail(generics.RetrieveUpdateDestroyAPIView):
    model = Categoria
    serializer_class = CategoriaSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class ProdutoList(generics.ListCreateAPIView):
    model = Produto
    serializer_class = ProdutoSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

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