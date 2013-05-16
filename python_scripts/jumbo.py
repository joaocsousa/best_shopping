import re, requests, hiper, time
from bs4 import BeautifulSoup, Tag
from utils import Utils
from hipers import models
from django.utils import timezone
import random
from getproxies import GetProxies
from random import choice

class Jumbo(hiper.Hiper):

    #constructor
    def __init__(self):
        self._name = "Jumbo"
        self._domain = "http://www.jumbo.pt"
        self._mainPath = "Frontoffice/ContentPages/JumboNetWelcome.aspx"
        self._url = Utils.toStr(self._domain + "/" + self._mainPath)

        hiper.Hiper.__init__(self, name=self._name, domain=self._domain, mainPath=self._mainPath)

        # Save to DB
        hiperDB = models.Hiper(nome=self._name, domain=self._domain, mainPath=self._mainPath, latest_update=timezone.now())
        Utils.saveObjToDB(hiperDB)

        self._hiperRef = hiperDB

        self._session = requests.Session()
        self._viewStateKey = ""

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
        return None

    def _updateViewStateKey(self, soupObj):
        viewStateKeyObj = soupObj.find("input", { "id" : "__VIEWSTATE_KEY" })
        try:
            if viewStateKeyObj["value"] != self._viewStateKey:
                self._viewStateKey = viewStateKeyObj["value"]
                Utils.printMsg(self._name, "ViewStateKey updated: " + self._viewStateKey, Utils.getLineNo())
        except:
            pass

    def startFetchingProducts(self):
        start_time = time.time()
        Utils.printMsg(self._name, "Started", Utils.getLineNo())
        success = False
        while (success == False):
            try:
                while True:
                    proxy = self._getProxy()
                    try:
                        jumboMainPage = Utils.makeGetRequest(self._session, self._url, proxy)
                        break
                    except requests.ConnectionError:
                        print traceback.format_exc()
                        self._failedProxies.append(proxy)
                        pass
                soupJumbo = BeautifulSoup(jumboMainPage)
                categorias = soupJumbo.findAll("a", { "class" : "btCategoria" })
                self._updateViewStateKey(soupJumbo)
                success = True
            except Exception, e:
                Utils.printMsg(self._name, "Nao consegui encontrar categorias!", Utils.getLineNo())
                Utils.printMsg(self._name, str(e), Utils.getLineNo())
        currentCat = 1
        for categoria in categorias:

            # Categoria - URL
            try:
                catUrl = Utils.strip(categoria['href'])
            except:
                catUrl = None

            # Categoria - Nome
            try:
                catName = Utils.strip(categoria.find("div", { "class" : "titCat" }).string)
            except Exception, e:
                catName = None

            Utils.printMsg(self._name, "Categoria [" + catName + "]", Utils.getLineNo())

            # Save to DB
            catDB = models.Categoria(url=catUrl, nome=catName, categoria_pai=None, hiper=self._hiperRef, latest_update=timezone.now())
            Utils.saveObjToDB(catDB)

            # Categoria - SubCategorias
            subCategorias = soupJumbo.findAll("a", id = re.compile(r"subItem_"+str(currentCat)+"_"))
            currentSubCat = 1
            for subCategoria in subCategorias:

                # SubCategoria - Nome
                try:
                    subCatName = Utils.strip(subCategoria.text)
                except:
                    subCatName = None

                Utils.printMsg(self._name, "SubCategoria [" + subCatName + "]", Utils.getLineNo())

                # SubCategoria - URL
                try:
                    subCatUrl = Utils.strip(subCategoria['href'])
                except:
                    subCatUrl = None

                # Save to DB
                subCatDB = models.Categoria(url=subCatUrl, nome=subCatName, categoria_pai=catDB, hiper=self._hiperRef, latest_update=timezone.now())
                Utils.saveObjToDB(subCatDB)

                # SubCategoria - SubSubCategorias
                subSubCategorias = soupJumbo.findAll("div", id = re.compile(r"subsubCategorias_"+str(currentCat)+"_"+str(currentSubCat)+"$"))
                currentSubSubCat = 1
                for subSubCategoriaGroup in subSubCategorias:
                    for subSubCategoria in subSubCategoriaGroup:

                        if len(Utils.strip(subSubCategoria.string))>0:

                            # SubSubCategoria - Nome
                            try:
                                subSubCatName = Utils.strip(subSubCategoria.string)
                            except:
                                subSubCatName = None

                            Utils.printMsg(self._name, "SubSubCategoria [" + subSubCatName + "]", Utils.getLineNo())

                            # SubSubCategoria - URL
                            try:
                                subSubCatUrl = Utils.strip(subSubCategoria["href"])
                            except:
                                subSubCatUrl = None

                            # Save to DB
                            subSubCatDB = models.Categoria(url=subSubCatUrl, nome=subSubCatName, categoria_pai=subCatDB, hiper=self._hiperRef, latest_update=timezone.now())
                            Utils.saveObjToDB(subSubCatDB)

                            # SubSubCategoria - Produtos
                            self._getProdutosFromCat(subSubCatDB)

                    currentSubSubCat+=1
                currentSubCat+=1
            currentCat+=1

            Utils.printMsg(self._name, 'Finished fetching products of: ' + Utils.toStr(catName), Utils.getLineNo())

        Utils.printMsg(self._name, "-" + "Finished - Elapsed: " + str(time.time()-start_time) + " seconds", Utils.getLineNo())

    def _getProdutosFromCat(self, catDB):

        if not Utils.validUrl(catDB.url):
            return False

        currentPage = ""
        nextPage = "1"
        singlePage = False
        while nextPage != "" and nextPage != currentPage and singlePage == False:
            productsPage = self._getProdutsPage(nextPage, catDB.url)
            productsPageSoup = BeautifulSoup(productsPage)
            self._updateViewStateKey(productsPageSoup)
            #update current page and next page
            paginasSoup = productsPageSoup.find("div", {"class" : "pag"})
            if paginasSoup is None:
                #aqui nao ha produtos, sai do ciclo
                break
            paginas = paginasSoup.find("div", {"class": "num"}).findChildren()
            if len(paginas) == 0:
                singlePage = True
                currentPage = "1"
            for pagina in paginas:
                try:
                    if pagina['class'][0] == 'pagSelec':
                        currentPage = pagina.string
                        break
                except:
                    nextPage = pagina.string
                    pass

            #we have currentPage and NextPage

            #parse products for this page
            nrProdutosParsed = 0
            products = productsPageSoup.findAll("div", {"class" : "produtoLista"})
            for product in products:

                # Produto - Nome
                try:
                    nome = Utils.strip(product.find("a", {"class" : "titProd"}).text)
                    if nome == "":
                        raise Exception("")
                except:
                    nome = None
                    #Skip this product if it is ineligable
                    continue

                # Produto - URL
                try:
                    urlProduto = Utils.strip(product.find("a", {"class" : "titProd"})["href"])
                except:
                    urlProduto = None

                # Produto - Preco
                try:
                    precoTxt = Utils.strip(product.find("div", {"class" : "preco"}).text)
                    precos = re.findall(r'\d+', precoTxt)
                    precoProduto = (float(precos[0])*100+float(precos[1]))/100
                except:
                    precoProduto = None

                # Produto - Preco/Kg
                try:
                    precoKgTxt = Utils.strip(product.find("div", {"class" : "prodkg"}).text)
                    precoKg = float(re.findall(ur"\d{1,4}[,.]\d{1,4}", precoKgTxt)[0].replace(",","."))
                except:
                    precoKg = None

                # Produto - Peso
                try:
                    peso = Utils.strip(product.find("div", {"class" : "gr"}).text)
                except:
                    peso = None

                # Produto - ID
                try:
                    idProduto = Utils.strip(re.findall(ur"\d+$", url)[0])
                except:
                    idProduto = -1

                # Produto - Imagem
                try:
                    imagem = self._domain + Utils.strip(product.find("a", {"id" : "lProdDetail"}).find("img")["src"])
                except:
                    imagem = None

                # Produto - Marca
                try:
                    marca = Utils.strip(product.find("div", {"class" : "titMarca"}).text).lower()
                except:
                    marca = None

                # Save to DB
                produtoDB = models.Produto( nome=nome,
                                            marca=marca,
                                            preco=precoProduto,
                                            preco_kg=precoKg,
                                            peso=peso,
                                            url_pagina=urlProduto,
                                            url_imagem=imagem,
                                            desconto=None,
                                            categoria_pai=catDB,
                                            hiper=self._hiperRef,
                                            latest_update=timezone.now())
                Utils.saveObjToDB(produtoDB)

                Utils.logProdutos(self._name, Utils.toStr(nome) + Utils.logSeparator + Utils.toStr(marca) + Utils.logSeparator + Utils.toStr(precoProduto) + Utils.logSeparator + Utils.toStr(precoKg) + Utils.logSeparator + Utils.toStr("") + Utils.logSeparator + Utils.toStr(peso) + Utils.logSeparator + Utils.toStr(idProduto) + Utils.logSeparator + Utils.toStr(urlProduto) + Utils.logSeparator + Utils.toStr(imagem))

                nrProdutosParsed += 1

            Utils.printMsg(self._name, catDB.nome+"-"+"Pagina [" + currentPage + "]: " + str(nrProdutosParsed) + " produtos", Utils.getLineNo())

        #new page, reset session
        self._session = requests.Session()

    def _getProdutsPage(self, page, url):
        payload = {
                        'M$M$sM1' : 'M$M$sM1|M$M$lPH$lCPH$cCatBrw$cPL$btPagCommd',
                        'hdnPrdListData': '1$$'+page,
                        'orderby':'ASC',
                        'produtos':'48',
                        'orderby':'PriceCapacityRatio',
                        'orderby':'ASC',
                        'produtos':'48',
                        '__EVENTTARGET': 'M$M$lPH$lCPH$cCatBrw$cPL$btPagCommd',
                        '__VIEWSTATE_KEY': self._viewStateKey
                    }
        if page == "1":
            payload['hdnPrdListData'] = "4$$48"
            while True:
                proxy = self._getProxy()
                try:
                    Utils.makePostRequest(self._session, url, payload, proxy) #request to update cookies
                    break
                except requests.ConnectionError:
                    print traceback.format_exc()
                    self._failedProxies.append(proxy)
                    pass
            while True:
                proxy = self._getProxy()
                try:
                    Utils.makePostRequest(self._session, url, payload, proxy) #request to update cookies
                    break
                except requests.ConnectionError:
                    print traceback.format_exc()
                    self._failedProxies.append(proxy)
                    pass
        while True:
            proxy = self._getProxy()
            try:
                request = Utils.makePostRequest(self._session, url, payload, proxy) #real request
                break
            except requests.ConnectionError:
                print traceback.format_exc()
                self._failedProxies.append(proxy)
                pass
        return request
