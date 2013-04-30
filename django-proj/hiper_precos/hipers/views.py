from hipers.models import Hiper, Categoria, Produto
from hipers.serializers import HiperSerializer, HiperResultSerializer, CategoriaSerializer, CategoriaListSerializer, ProdutoResultSerializer, ProdutoSerializer
from rest_framework import generics, permissions, renderers
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse
from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from django.http.response import HttpResponse
from hiper_precos.utils import Utils
from django.shortcuts import render_to_response
from django.views.decorators.csrf import csrf_exempt
import difflib

class HiperList(generics.ListCreateAPIView):
    model = Hiper
    serializer_class = HiperResultSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class HiperDetail(generics.RetrieveUpdateDestroyAPIView):
    model = Hiper
    serializer_class = HiperSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

class CategoriaList(generics.ListCreateAPIView):
    model = Categoria
    serializer_class = CategoriaListSerializer
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

    paginate_by = 10

    def get_queryset(self):
        queryset = Produto.objects.all()
        hiper = self.request.QUERY_PARAMS.get('hiper', None)
        if hiper is not None:
            queryset = queryset.filter(hiper=hiper)
        categoria_pai = self.request.QUERY_PARAMS.get('categoria_pai', None)
        if categoria is not None:
            queryset = queryset.filter(categoria_pai=categoria_pai)
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

class JSONResponse(HttpResponse):
    """
    An HttpResponse that renders it's content into JSON.
    """
    def __init__(self, data, **kwargs):
        content = JSONRenderer().render(data)
        kwargs['content_type'] = 'application/json'
        super(JSONResponse, self).__init__(content, **kwargs)

def search(request):
    errors = []
    if 'q' in request.GET:
        q = request.GET['q']
        if not q:
            errors.append('Enter a search term.')
        elif len(q) > 50:
            errors.append('Please enter at most 20 characters.')
        else:
            produtos = Produto.objects.all()
            categorias = Categoria.objects.all()
            prodPorNome = produtos.filter(nome__icontains=q)
            prodPorMarca = produtos.filter(marca__icontains=q)
            categorias = categorias.filter(nome__icontains=q)

            dict = {
                'prodPorNome': ProdutoResultSerializer(prodPorNome).data,
                'prodPorMarca': ProdutoResultSerializer(prodPorMarca).data,
                'categorias' : CategoriaListSerializer(categorias).data,
            }

            return JSONResponse(dict)

    return render_to_response('search_form.html', {'errors': errors})

@api_view(('GET',))
def api_root(request, format=None):
    return Response({
            'hipers': reverse('hiper-list', request=request, format=format),
            'categorias': reverse('categoria-list', request=request, format=format),
            'produtos': reverse('produto-list', request=request, format=format)
        })

# this method has the returning the current database to write to
def get_db_to_write_to(request, format=None):
    return HttpResponse(Utils.getDbToWriteTo());

# this method has the purpose of informing the server
# that the database was updated to update the log file
def hipers_updated(request, format=None):
    # try:
    Utils.dbPopulated()
    return HttpResponse("OK");
    # except:
    #     pass
    # return HttpResponse("FAILED");
