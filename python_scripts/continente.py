import re, requests, hiper, time, codecs, traceback
from bs4 import BeautifulSoup
from utils import Utils
from hipers import models
from django.utils import timezone
import random
from getproxies import GetProxies
from random import choice

class Continente(hiper.Hiper):

    #constructor
    def __init__(self):
        self._name = "Continente"
        self._domain = "http://www.continente.pt"
        self._mainPath = "stores/continente/pt-pt/public/Pages/homepage.aspx"
        self._url = Utils.toStr(self._domain + "/" + self._mainPath)

        hiper.Hiper.__init__(self, name=self._name, domain=self._domain, mainPath=self._mainPath)

        # Save to DB
        hiperDB = models.Hiper(nome=self._name, domain=self._domain, mainPath=self._mainPath, latest_update=timezone.now())
        Utils.saveObjToDB(hiperDB)

        self._hiperRef = hiperDB

        self._session = requests.Session()

        self._useProxies = False

        self._resetProxies()

    def _resetProxies(self):
        if self._useProxies:
            self._proxies = GetProxies().getVerifiedProxies()
            self._failedProxies = []

    def _checkProxies(self):
        if self._useProxies and self._failedProxies and set(self._failedProxies).issubset(self._proxies):
            self._resetProxies()

    def _getProxy(self):
        if self._useProxies:
            self._checkProxies()
            proxy = choice(self._proxies)
            while (proxy in self._failedProxies):
                proxy = choice(self._proxies)
            return proxy
        else:
            return None

    def startFetchingProducts(self):
        start_time = time.time()

        Utils.printMsg(self._name, "Started", Utils.getLineNo())

        success = False
        while (success == False):
            try:
                while True:
                    proxy = self._getProxy()
                    try:
                        continentMainPage = Utils.makeGetRequest(self._session, self._url, proxy)
                        break
                    except requests.ConnectionError:
                        print traceback.format_exc()
                        self._failedProxies.append(proxy)
                        pass
                soupContinente = BeautifulSoup(continentMainPage, "lxml")
                categorias = soupContinente.find("ul", { "id" : "categoryMenu" }).findAll("li")
                success = True
            except Exception, e:
                Utils.printMsg(self._name, "Nao consegui encontrar categorias!", Utils.getLineNo())
                Utils.printMsg(self._name, str(e), Utils.getLineNo())
        for cat in categorias:
            
            # Categoria - URL
            # try:
            #     catUrl = self._domain+"/"+Utils.strip(cat['href'])
            # except:
            #     catUrl = None

            # # Categoria - Abre Categoria
            # while True:
            #     proxy = self._getProxy()
            #     try:
            #         catRequest = Utils.makeGetRequest(self._session, catUrl, proxy)
            #         break
            #     except requests.ConnectionError:
            #         print traceback.format_exc()
            #         self._failedProxies.append(proxy)
            #         pass
            # soupCat = BeautifulSoup(catRequest)

            # Categoria - Nome
            # try:
            #     catName = Utils.strip(soupCat.find("a", {"class": "navmainOpcoesMenu"}).text)
            # except Exception, e:
            #     catName = None

            # # Save to DB
            # catDB = models.Categoria(url=catUrl, nome=catName, categoria_pai=None, hiper=self._hiperRef, latest_update=timezone.now())
            # Utils.saveObjToDB(catDB)

            # Utils.printMsg(self._name, "Categoria [" + catName + "]", Utils.getLineNo())

            # # Categoria - SubCategorias
            # subCats = soupCat.find("div", {"id": "subcatNav"}).findAll("div", recursive=False)
            # for subCat in subCats:

            #     if "button" in subCat["class"]:

            #         # SubCategoria - Nome
            #         try:
            #             subCatName = Utils.strip(subCat.text)
            #         except:
            #             subCatName = None

            #         Utils.printMsg(self._name, "SubCategoria [" + subCatName + "]", Utils.getLineNo())

            #         # SubCategoria - URL
            #         try:
            #             subCatUrl = self._domain+"/"+Utils.strip(subCat.find("a")["href"])
            #         except:
            #             subCatUrl = None

            #         # Save to DB
            #         subCatDB = models.Categoria(url=subCatUrl, nome=subCatName, categoria_pai=catDB, hiper=self._hiperRef, latest_update=timezone.now())
            #         Utils.saveObjToDB(subCatDB)

            #         # SubCategoria - Produtos
            #         while(self._getProdutosFromCat(subCatDB) == False):
            #             pass

            #     elif "menu" in subCat["class"]:
            #         subSubCats = subCat.findAll("div", {"class": "menuNode"}, recursive=False)
            #         for subSubCat in subSubCats:

            #             # SubSubCategoria - Nome
            #             try:
            #                 subSubCatName = Utils.strip(subSubCat.text)
            #             except:
            #                 subSubCatName = None

            #             Utils.printMsg(self._name, "SubSubCategoria [" + subSubCatName + "]", Utils.getLineNo())

            #             # SubSubCategoria - URL
            #             try:
            #                 subSubCatUrl = self._domain+"/"+Utils.strip(subSubCat.find("a")["href"])
            #             except:
            #                 subSubCatUrl = None

            #             # Save to DB
            #             subSubCatDB = models.Categoria(url=subSubCatUrl, nome=subSubCatName, categoria_pai=subCatDB, hiper=self._hiperRef, latest_update=timezone.now())
            #             Utils.saveObjToDB(subSubCatDB)

            #             # SubSubCategoria - Produtos
            #             while(self._getProdutosFromCat(subSubCatDB) == False):
            #                 pass
            #             try:
            #                 subSubSubCats = subCat.find("div", {"id" : subSubCat["id"]+"Menu", "class": "menu"}, recursive=False).findAll("div", {"class": "menuNode"}, recursive=False)
            #                 for subSubSubCat in subSubSubCats:

            #                     # SubSubSubCategoria - Nome
            #                     try:
            #                         subSubSubCatName = Utils.strip(subSubSubCat.text)
            #                     except:
            #                         subSubSubCatName = None

            #                     Utils.printMsg(self._name, "SubSubSubCategoria [" + subSubSubCatName + "]", Utils.getLineNo())

            #                     # SubSubSubCategoria - URL
            #                     try:
            #                         subSubSubCatUrl = self._domain+"/"+Utils.strip(subSubSubCat.find("a")["href"])
            #                     except:
            #                         subSubSubCatUrl = None

            #                     # Save to DB
            #                     subSubSubCatDB = models.Categoria(url=subSubSubCatUrl, nome=subSubSubCatName, categoria_pai=subSubCatDB, hiper=self._hiperRef, latest_update=timezone.now())
            #                     Utils.saveObjToDB(subSubSubCatDB)

            #                     # SubSubSubCategoria - Produtos
            #                     while(self._getProdutosFromCat(subSubSubCatDB) == False):
            #                         pass

            #             except KeyError:
            #                 pass

            # # reset session
            # self._session = requests.Session()
            # while True:
            #     proxy = self._getProxy()
            #     try:
            #         Utils.makeGetRequest(self._session, self._url, proxy)
            #         break
            #     except requests.ConnectionError:
            #         print traceback.format_exc()
            #         self._failedProxies.append(proxy)
            #         pass

            # Utils.printMsg(self._name, 'Finished fetching products of: ' + Utils.toStr(catName), Utils.getLineNo())

        Utils.printMsg(self._name, "-" + "Finished - Elapsed: " + str(time.time()-start_time) + " seconds", Utils.getLineNo())

    def _getProdutosFromCat(self, catDB, pagina=1, nrPages=None):

        if not Utils.validUrl(catDB.url):
            return True

        if pagina != 1 and nrPages+1 == pagina:
            return True

        payload = {
                    '__EVENTTARGET': 'ProductsMain1:DataListPages:_ctl'+str((pagina - 1) * 2)+':linkButton',
                    'ProductsMain1:cmbPaginacao':'48'
                    }
        while True:
            proxy = self._getProxy()
            try:
                request = Utils.makePostRequest(self._session, catDB.url, payload, proxy)
                break
            except requests.ConnectionError:
                self._failedProxies.append(proxy)
                print traceback.format_exc()
        soupPagina = BeautifulSoup(request)
        error = soupPagina.find("span",{"id":"Error1_lblErrorDescription"})
        if error:
            Utils.printMsg(self._name, "ERROR IN REQUEST", Utils.getLineNo())
            return False

        #parse Produtos
        produtos = soupPagina.findAll("div",{"class":"product-view"})

        nrProdutosParsed = 0

        for produto in produtos:

            # Produto - Nome
            try:
                nome = Utils.strip(produto.find("a", {"class":"product-view-text-item"}).find(text=True))
                if nome == "":
                    raise Exception("")
            except:
                nome = None
                #Skip this product if it is ineligable
                continue

            # Produto - URL
            try:
                urlProduto = Utils.strip(produto.find("a")["href"])
                urlProduto = self._domain + "/" + urlProduto
                if urlProduto == "":
                    raise Exception("")
            except:
                urlProduto = None

            # Produto - Preco
            try:
                precoProduto = float(re.findall(r'\d*[.,]\d*', Utils.strip(produto.find("div",{"class":"product-view-price"}).text).replace(",","."))[0])
            except:
                precoProduto = None

            # Produto - Preco/Kg
            try:
                precoKg = float(re.findall(r'\d*[.,]\d*', Utils.strip(produto.find("span",{"class":"produtoListaPrecoUnit"}).text).replace(",","."))[0])
            except:
                precoKg = None

            # Produto - Peso
            try:
                peso = Utils.strip(produto.find("span",{"class":"product-package"}).text)
            except:
                peso = None

            # Produto - ID
            try:
                idProduto = int(re.findall(r'productId=\d+', urlProduto, re.IGNORECASE)[0].replace("productId=",""))
            except:
                idProduto = -1

            # Produto - Imagem
            try:
                imagem = self._domain + Utils.strip(produto.find("img")["src"].replace("\\","/").replace("/med/","/lar/").replace("/Med/","/Lar/").replace("_med","_lar").replace("_Med","_Lar"))
                if imagem == "":
                    raise Exception("")
            except:
                imagem = None

            # Produto - Marca
            try:
                marca = Utils.strip(produto.find("span",{"class":"product-logo"}).text)
                if marca == "":
                    raise Exception("")
            except:
                marca = None

            # Produto - Desconto
            try:
                fraseDesconto = re.findall(r'desconto\s*.*\d*[.,]\d*', Utils.strip(produto.text), re.IGNORECASE)[0]
                desconto = float(re.findall(r'\d*[.,]\d*', fraseDesconto)[0].replace(",","."))
            except:
                desconto = None

            # Save to DB
            produtoDB = models.Produto( nome=nome,
                                        marca=marca,
                                        preco=precoProduto,
                                        preco_kg=precoKg,
                                        peso=peso,
                                        url_pagina=urlProduto,
                                        url_imagem=imagem,
                                        desconto=desconto,
                                        categoria_pai=catDB,
                                        hiper=self._hiperRef,
                                        latest_update=timezone.now())
            Utils.saveObjToDB(produtoDB)

            Utils.logProdutos(self._name, Utils.toStr(nome) + Utils.logSeparator + Utils.toStr(marca) + Utils.logSeparator + Utils.toStr(precoProduto) + Utils.logSeparator + Utils.toStr(precoKg) + Utils.logSeparator + Utils.toStr(desconto) + Utils.logSeparator + Utils.toStr(peso) + Utils.logSeparator + Utils.toStr(idProduto) + Utils.logSeparator + Utils.toStr(urlProduto) + Utils.logSeparator + Utils.toStr(imagem))

            nrProdutosParsed += 1

        Utils.printMsg(self._name, catDB.nome+"-"+"Pagina [" + str(pagina) + "]: " + str(nrProdutosParsed) + " produtos", Utils.getLineNo())

        try:
            paginas = soupPagina.find("span",{"id":"ProductsMain1_DataListPages"}).findAll(id=re.compile('.*_DataListPages__.*'))
        except:
            return True

        self._getProdutosFromCat(catDB, pagina+1, len(paginas))
