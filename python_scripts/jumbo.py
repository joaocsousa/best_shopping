import re, requests, hiper, time
from bs4 import BeautifulSoup, Tag
from utils import Utils
from hipers import models
from django.utils import timezone

class Jumbo(hiper.Hiper):        
    _session = None
    _viewStateKey = None
    _hiperRef = None
    #constructor
    def __init__(self):
        name = "Jumbo"
        domain = "http://www.jumbo.pt"
        mainPath = "Frontoffice/ContentPages/JumboNetWelcome.aspx"
        hiper.Hiper.__init__(self, name=name, domain=domain, mainPath=mainPath)
        
        # Save to DB
        hiperDB = models.Hiper(nome=name, domain=domain, mainPath=mainPath)
        hiperDB.save()
        self._hiperRef = hiperDB

        self._viewStateKey = ""
        self._session = requests.Session()
    def _updateViewStateKey(self, soupObj):
        viewStateKeyObj = soupObj.find("input", { "id" : "__VIEWSTATE_KEY" })
        try:
            if viewStateKeyObj["value"] != self._viewStateKey:
                self._viewStateKey = viewStateKeyObj["value"]
                self.printMsg("ViewStateKey updated: " + self._viewStateKey, Utils.getLineNo())
        except:
            pass
    def startFetchingProducts(self):
        start_time = time.time()
        self.printMsg("Started", Utils.getLineNo())
        jumboMainPage = self._session.get(self._url)
        soupJumbo = BeautifulSoup(jumboMainPage.text)
        self._updateViewStateKey(soupJumbo)
        try:
            categorias = soupJumbo.findAll("a", { "class" : "btCategoria" })
        except Exception, e:
            self.printMsg("Nao consegui encontrar categorias!", Utils.getLineNo())
            self.printMsg(str(e), Utils.getLineNo())
            raise SystemExit
        currentCat = 1
        for categoria in categorias:
            
            cat = hiper.Categoria()
            self.addCategoria(cat)
            
            # Categoria - Nome
            try:
                catName = categoria.find("div", { "class" : "titCat" }).string
            except Exception, e:
                self.printMsg(str(e), Utils.getLineNo())
                catName = None
            cat.setName(catName)

            self.printMsg("Categoria [" + catName + "]", Utils.getLineNo())

            # Categoria - URL
            try:
                catUrl = categoria['href']
            except:
                catUrl = None
            cat.setUrl(catUrl)

            # Categoria - ID
            try:
                catID = int(catUrl[catUrl.rfind('?C=')+len('?C='):])
            except:
                catID = self.getNewId()
            cat.setId(catID)
            self.addExistingId(int(catID))

            # Save to DB
            catDB = models.Categoria(url=catUrl, nome=catName, categoria_pai=None, hiper=self._hiperRef)
            catDB.save()

            # Categoria - SubCategorias
            subCategorias = soupJumbo.findAll("a", id = re.compile("subItem_"+str(currentCat)+"_"))
            currentSubCat = 1
            for subCategoria in subCategorias:
                
                currSubCat = hiper.Categoria()
                cat.addSubCategoria(currSubCat)

                # SubCategoria - Categoria Pai
                currSubCat.setCategoriaPai(cat.getId())

                # SubCategoria - Nome
                try:
                    subCatName = subCategoria.string
                    if subCatName is None:
                        for child in subCategoria.children:
                            if type(child) is Tag:
                                subCatName = child.contents[2].strip()
                                break
                except:
                    subCatName = None
                currSubCat.setName(subCatName)

                self.printMsg("SubCategoria [" + subCatName + "]", Utils.getLineNo())

                # SubCategoria - URL
                try:
                    subCatUrl = subCategoria['href']
                except:
                    subCatUrl = None
                currSubCat.setUrl(subCatUrl)

                # SubCategoria - ID
                try:
                    subCatID = int(subCatUrl[subCatUrl.rfind('?C=')+len('?C='):])
                except:
                    subCatID = self.getNewId()
                currSubCat.setId(subCatID)
                self.addExistingId(int(subCatID))

                # Save to DB
                subCatDB = models.Categoria(url=subCatUrl, nome=subCatName, categoria_pai=catDB, hiper=self._hiperRef)
                subCatDB.save()

                # SubCategoria - SubSubCategorias
                subSubCategorias = soupJumbo.findAll("div", id = re.compile("subsubCategorias_"+str(currentCat)+"_"+str(currentSubCat))) 
                currentSubSubCat = 1
                for subSubCategoriaGroup in subSubCategorias:
                    for subSubCategoria in subSubCategoriaGroup:
                        
                        if len(subSubCategoria.string.strip())>0:

                            currSubSubCat = hiper.Categoria()
                            currSubCat.addSubCategoria(currSubSubCat)

                            # SubSubCategoria - Categoria Pai
                            currSubSubCat.setCategoriaPai(currSubCat.getId())

                            # SubSubCategoria - Nome
                            try:
                                subSubCatName = subSubCategoria.string.strip()
                            except:
                                subSubCatName = None
                            currSubSubCat.setName(subSubCatName)
                        
                            self.printMsg("SubSubCategoria [" + subSubCatName + "]", Utils.getLineNo())

                            # SubSubCategoria - URL
                            try:
                                subSubCatUrl = subSubCategoria["href"]
                            except:
                                subSubCatUrl = None
                            currSubSubCat.setUrl(subSubCatUrl)
                
                            # SubSubCategoria - ID
                            try:
                                subSubCatID = int(subSubCatUrl[subSubCatUrl.rfind('?C=')+len('?C='):])
                            except:
                                subSubCatID = self.getNewId()
                            currSubSubCat.setId(subSubCatID)
                            self.addExistingId(int(subSubCatID))

                            # Save to DB
                            subSubCatDB = models.Categoria(url=subSubCatUrl, nome=subSubCatName, categoria_pai=subCatDB, hiper=self._hiperRef)
                            subSubCatDB.save()

                            # SubSubCategoria - Produtos
                            if Utils.validUrl(subCatUrl):
                                self._fetchProducts(currSubSubCat, subSubCatDB)
                            
                    currentSubSubCat+=1
                currentSubCat+=1
            currentCat+=1
        self.printMsg("Finished - Elapsed: " + str(time.time()-start_time) + " seconds", Utils.getLineNo())

    def _fetchProducts(self, categoria, catDB):
        currUrl = categoria.getUrl()
        currentPage = ""
        nextPage = "1"
        singlePage = False
        while nextPage != "" and nextPage != currentPage and singlePage == False:
            productsPage = self._getProdutsPage(nextPage, currUrl)
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
                    nome = product.find("a", {"class" : "titProd"}).text.strip()
                    if nome == "":
                        raise Exception("")
                except:
                    nome = None
                    #Skip this product if it is ineligable
                    continue

                # Produto - URL
                try:
                    urlProduto = product.find("a", {"class" : "titProd"})["href"]
                except Exception, e:
                    self.printMsg(str(e), Utils.getLineNo())
                    urlProduto = None

                # Produto - Preco
                try:
                    precoSoup = product.find("div", {"class" : "preco"})
                    precos = re.findall(r'\d+', precoSoup.text)
                    precoProduto = (float(precos[0])*100+float(precos[1]))/100
                except:
                    precoProduto = None

                # Produto - Preco/Kg
                try:
                    precoKgSoup = product.find("div", {"class" : "prodkg"})
                    regex = ur"\d{1,4}[,.]\d{1,4}"
                    precoKg = float(re.findall(regex, precoKgSoup.text.strip())[0].replace(",","."))
                except:
                    precoKg = None

                # Produto - Peso
                try:
                    peso = product.find("div", {"class" : "gr"}).text.strip()
                except:
                    peso = None

                # Produto - ID
                try:
                    regex = ur"\d+$"
                    idProduto = re.findall(regex, url)[0]
                    if self.idExists(idProduto):
                        raise Exception("")
                    self.addExistingId(str(idProduto))
                except:
                    idProduto = self.getNewId()
                    self.addExistingId(str(idProduto))

                # Produto - Imagem
                try:
                    imagem = self._domain + product.find("a", {"id" : "lProdDetail"}).find("img")["src"]
                except:
                    imagem = None

                # Produto - Marca
                try:
                    marcaSoup = product.find("div", {"class" : "titMarca"})
                    marca = marcaSoup.text.strip().lower()
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
                                            last_updated=timezone.now())
                produtoDB.save()

                Utils.logProdutos(self.getName(), Utils.toStr(nome) + Utils.logSeparator + Utils.toStr(marca) + Utils.logSeparator + Utils.toStr(precoProduto) + Utils.logSeparator + Utils.toStr(precoKg) + Utils.logSeparator + Utils.toStr("") + Utils.logSeparator + Utils.toStr(peso) + Utils.logSeparator + Utils.toStr(idProduto) + Utils.logSeparator + Utils.toStr(urlProduto) + Utils.logSeparator + Utils.toStr(imagem))
                
                nrProdutosParsed += 1
                
            self.printMsg("Pagina [" + str(currentPage) + "]: " + str(nrProdutosParsed) + " produtos", Utils.getLineNo())

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
            self._session.post(url, data=payload) #request to update cookies
            self._session.post(url, data=payload) #request to update cookies
        request = self._session.post(url, data=payload) #real request
        return request.text