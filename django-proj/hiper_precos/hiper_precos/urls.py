from django.conf.urls import patterns, include, url
from rest_framework.urlpatterns import format_suffix_patterns
from hipers import views

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

apiVersion = '0.6'
apiPath = 'api/'+apiVersion+'/'

urlpatterns = patterns('hipers.views',
    url(r'^'+apiPath+'$', 'api_root'),
    url(r'^'+apiPath+'hipers/$', views.HiperList.as_view(), name='hiper-list'),
    url(r'^'+apiPath+'hipers/(?P<pk>[0-9]+)/$', views.HiperDetail.as_view(), name='hiper-detail'),
    url(r'^'+apiPath+'categorias/$', views.CategoriaList.as_view(), name='categoria-list'),
    url(r'^'+apiPath+'categorias/(?P<pk>[0-9]+)/$', views.CategoriaDetail.as_view(), name='categoria-detail'),
    url(r'^'+apiPath+'produtos/$', views.ProdutoList.as_view(), name='produto-list'),
    url(r'^'+apiPath+'produtos/(?P<pk>[0-9]+)/$', views.ProdutoDetail.as_view(), name='produto-detail'),
)

urlpatterns = format_suffix_patterns(urlpatterns)

urlpatterns += patterns('',
    url(r'^'+apiPath+'api-auth/', include('rest_framework.urls', namespace='rest_framework')),
)