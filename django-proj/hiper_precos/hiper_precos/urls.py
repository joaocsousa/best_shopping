from django.conf.urls import patterns, include, url
from rest_framework.urlpatterns import format_suffix_patterns
from hipers import views
from django.conf import settings

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

apiPath = 'api/'

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

urlpatterns += patterns('hipers.views',
    url(r'^hipers_updated/$', views.hipers_updated),
    url(r'^get_db_to_write_to/$', views.get_db_to_write_to),
    url(r'^get_db_to_read_from/$', views.get_db_to_read_from),
    url(r'^search/$', views.search),
    url(r'^get_latest_update/$', views.get_latest_update),
)

if settings.DEBUG:
    # static files (images, css, javascript, etc.)
    urlpatterns += patterns('', (r'^media/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.MEDIA_ROOT}))
