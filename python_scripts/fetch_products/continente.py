import re, requests, hiper, time
from bs4 import BeautifulSoup
from utils import Utils
from hipers import models
from threading import Thread, Lock
from django.utils import timezone
from multiprocessing import Process

class ProdSpider(Thread):

    def __init__(self, hiperName, catUrl, catDB, session, lock):
        Thread.__init__(self)
        self._catUrl = catUrl
        self._catDB = catDB
        self._lock = lock
        self._session = session
        self._hiperName = hiperName
    def run(self):
        self._getProdutosFromCat()

    def _getProdutosFromCat(self, pagina=1, nrPages=None):

        if not Utils.validUrl(self._catUrl):
            return False

        if pagina != 1 and nrPages+1 == pagina:
            return

        payload = {
                    '__EVENTTARGET': 'ProductsMain1:DataListPages:_ctl'+str((pagina - 1) * 2)+':linkButton',
                    'ProductsMain1:cmbPaginacao':'48'
                    }
        #self._lock.acquire()
        request = self._session.post(self._catUrl, data=payload) #real request
        #self._lock.release()
        soupPagina = BeautifulSoup(request.text)
        error = soupPagina.find("span",{"id":"Error1_lblErrorDescription"})
        if error:
            Utils.printMsg(self._hiperName, "ERROR IN REQUEST", Utils.getLineNo())
            print error.text
            return False

        #parse Produtos
        produtos = soupPagina.findAll("div",{"class":"product-view"})
        
        nrProdutosParsed = 0

        for produto in produtos:
            
            # Produto - Nome
            try:
                nome = produto.find("a", {"class":"product-view-text-item"}).find(text=True).strip()
                if nome == "":
                    raise Exception("")
            except:
                nome = None
                #Skip this product if it is ineligable
                continue
            
            # Produto - URL
            try:
                urlProduto = produto.find("a")["href"].strip()
                urlProduto = " ".join(urlProduto.split())
                urlProduto = self._hiperDomain + "/" + urlProduto
                if urlProduto == "":
                    raise Exception("")
            except:
                urlProduto = None
            
            # Produto - Preco
            try:
                precoProduto = float(re.findall(r'\d*[.,]\d*', produto.find("div",{"class":"product-view-price"}).text.replace(",","."))[0])
            except:
                precoProduto = None
            
            # Produto - Preco/Kg
            try:
                precoKg = float(re.findall(r'\d*[.,]\d*', produto.find("span",{"class":"produtoListaPrecoUnit"}).text.replace(",","."))[0])
            except:
                precoKg = None

            # Produto - Peso
            try:
                peso = produto.find("span",{"class":"product-package"}).text
            except:
                peso = None

            # Produto - ID
            try:
                idProduto = int(re.findall(r'productId=\d+', urlProduto, re.IGNORECASE)[0].replace("productId=",""))
            except:
                idProduto = -1

            # Produto - Imagem
            try:
                imagem = self._hiperDomain + "/" + produto.find("img")["src"].replace("\\","/").replace("Med","Lar").replace("med","lar")
                if imagem == "":
                    raise Exception("")
            except:
                imagem = None

            # Produto - Marca
            try:
                marca = produto.find("span",{"class":"product-logo"}).text.strip()
                marca = " ".join(marca.split())
                if marca == "":
                    raise Exception("")
            except:
                marca = None

            # Produto - Desconto
            try:
                fraseDesconto = re.findall(r'desconto\s*.*\d*[.,]\d*', produto.text.strip(), re.IGNORECASE)[0]
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
                                        categoria_pai=self._catDB,
                                        last_updated=timezone.now())
            produtoDB.save()

            Utils.logProdutos(self._hiperName, Utils.toStr(nome) + Utils.logSeparator + Utils.toStr(marca) + Utils.logSeparator + Utils.toStr(precoProduto) + Utils.logSeparator + Utils.toStr(precoKg) + Utils.logSeparator + Utils.toStr(desconto) + Utils.logSeparator + Utils.toStr(peso) + Utils.logSeparator + Utils.toStr(idProduto) + Utils.logSeparator + Utils.toStr(urlProduto) + Utils.logSeparator + Utils.toStr(imagem))        
            
            nrProdutosParsed += 1

        Utils.printMsg(self._hiperName, self._catDB.nome+"-"+"Pagina [" + str(pagina) + "]: " + str(nrProdutosParsed) + " produtos", Utils.getLineNo())

        if nrProdutosParsed == 0:
            raise SystemExit

        try:
            paginas = soupPagina.find("span",{"id":"ProductsMain1_DataListPages"}).findAll(id=re.compile('.*_DataListPages__.*'))
        except:
            return False

        self._getProdutosFromCat(pagina+1, len(paginas))

class CatSpider(Process):
    
    def __init__(self, categoria, hiperName, hiperDomain, hiperMainPath, hiperRef):
        Process.__init__(self)
        self._categoria = categoria
        self._hiperName = hiperName
        self._hiperDomain = hiperDomain
        self._hiperMainPath = hiperMainPath
        self._hiperRef = hiperRef
        self._threads = []
        self._session = requests.Session()
        self._lock = Lock()
    
    def run(self):

        # Categoria - URL
        try:
            catUrl = self._hiperDomain+"/"+self._categoria['href'].strip()
        except:
            catUrl = None

        # inicia a sessao
        self._session.get(self._hiperDomain+"/"+self._hiperMainPath)

        # Categoria - Abre Categoria
        catRequest = self._session.get(catUrl)
        soupCat = BeautifulSoup(catRequest.text)

        # Categoria - Nome
        try:
            catName = soupCat.find("a", {"class": "navmainOpcoesMenu"}).text
        except:
            catName = None

        # Save to DB
        catDB = models.Categoria(url=catUrl, nome=catName, categoria_pai=None, hiper=self._hiperRef)
        catDB.save()

        Utils.printMsg(self._hiperName, "Categoria [" + catName + "]", Utils.getLineNo())

        # Categoria - SubCategorias
        subCats = soupCat.find("div", {"id": "subcatNav"}).findAll("div", recursive=False)
        for subCat in subCats:
            
            if "button" in subCat["class"]:

                # SubCategoria - Nome
                try:
                    subCatName = subCat.text
                except:
                    subCatName = None

                Utils.printMsg(self._hiperName, "SubCategoria [" + subCatName + "]", Utils.getLineNo())

                # SubCategoria - URL
                try:
                    subCatUrl = self._hiperDomain+"/"+subCat.find("a")["href"]
                except:
                    subCatUrl = None
                
                # Save to DB
                subCatDB = models.Categoria(url=subCatUrl, nome=subCatName, categoria_pai=catDB, hiper=self._hiperRef)
                subCatDB.save()

                # SubCategoria - Produtos
                prodSpider = ProdSpider(self._hiperName, subCatUrl, subCatDB, self._session, self._lock)
                self._threads.append(prodSpider)
                prodSpider.start()

                if (len(self._threads) >= 10 ):
                    self._waitForThreads()

            elif "menu" in subCat["class"]:
                subSubCats = subCat.findAll("div", {"class": "menuNode"}, recursive=False)
                for subSubCat in subSubCats:
                
                    # SubSubCategoria - Nome
                    try:
                        subSubCatName = subSubCat.text
                    except:
                        subSubCatName = None

                    Utils.printMsg(self._hiperName, "SubSubCategoria [" + subSubCatName + "]", Utils.getLineNo())

                    # SubSubCategoria - URL
                    try:
                        subSubCatUrl = self._hiperDomain+"/"+subSubCat.find("a")["href"]
                    except:
                        subSubCatUrl = None

                    # Save to DB
                    subSubCatDB = models.Categoria(url=subSubCatUrl, nome=subSubCatName, categoria_pai=subCatDB, hiper=self._hiperRef)
                    subSubCatDB.save()

                    # SubSubCategoria - Produtos
                    prodSpider = ProdSpider(self._hiperName, subSubCatUrl, subSubCatDB, self._session, self._lock)
                    self._threads.append(prodSpider)
                    prodSpider.start()

                    if (len(self._threads) >= 10 ):
                        self._waitForThreads()

                    try:
                        subSubSubCats = subCat.find("div", {"id" : subSubCat["id"]+"Menu", "class": "menu"}, recursive=False).findAll("div", {"class": "menuNode"}, recursive=False)
                        for subSubSubCat in subSubSubCats:
                            
                            # SubSubSubCategoria - Nome
                            try:
                                subSubSubCatName = subSubSubCat.text
                            except:
                                subSubSubCatName = None

                            Utils.printMsg(self._hiperName, "SubSubSubCategoria [" + subSubSubCatName + "]", Utils.getLineNo())

                            # SubSubSubCategoria - URL
                            try:
                                subSubSubCatUrl = self._hiperDomain+"/"+subSubSubCat.find("a")["href"]
                            except:
                                subSubSubCatUrl = None

                            # Save to DB
                            subSubSubCatDB = models.Categoria(url=subSubSubCatUrl, nome=subSubSubCatName, categoria_pai=subSubCatDB, hiper=self._hiperRef)
                            subSubSubCatDB.save()

                            # SubSubSubCategoria - Produtos
                            prodSpider = ProdSpider(self._hiperName, subSubSubCatUrl, subSubSubCatDB, self._session, self._lock)
                            self._threads.append(prodSpider)
                            prodSpider.start()

                            if (len(self._threads) >= 10 ):
                                self._waitForThreads()

                    except KeyError:
                        pass
        Utils.printMsg(self._hiperName, 'Finished fetching products of: ' + Utils.toStr(catName), Utils.getLineNo())

    def _waitForThreads(self):
        #threads created
        for thread in self._threads:
            if thread.isAlive():
                thread.join()
        self._threads[:] = []

class Continente(hiper.Hiper):

    #constructor
    def __init__(self):
        self._name = "Continente"
        self._domain = "http://www.continente.pt"
        self._mainPath = "HomePage.aspx"
        self._url = Utils.toStr(self._domain + "/" + self._mainPath)
        self._threads = []

        hiper.Hiper.__init__(self, name=self._name, domain=self._domain, mainPath=self._mainPath)
        
        # Save to DB
        hiperDB = models.Hiper(nome=self._name, domain=self._domain, mainPath=self._mainPath)
        hiperDB.save()
        self._hiperRef = hiperDB

        self._session = requests.Session()

    def startFetchingProducts(self):
        start_time = time.time()
        Utils.printMsg(self._name, "Started", Utils.getLineNo())
        continentMainPage = self._session.get(self._url)
        soupContinente = BeautifulSoup(continentMainPage.text)
        try:
            categorias = soupContinente.find("table", { "id" : "Table4" }).findAll("a")
        except Exception, e:
            Utils.printMsg(self._name, "Nao consegui encontrar categorias!", Utils.getLineNo())
            Utils.printMsg(self._name, str(e), Utils.getLineNo())
            raise SystemExit
        for cat in categorias:
            catSpider = CatSpider(cat, self._name, self._domain, self._mainPath, self._hiperRef)
            self._threads.append(catSpider)
            catSpider.start()
            break
        #threads created
        #for thread in self._threads:
        #    if thread.isAlive():
        #        thread.join()

        Utils.printMsg(self._name, "/" + "Finished - Elapsed: " + str(time.time()-start_time) + " seconds", Utils.getLineNo())

