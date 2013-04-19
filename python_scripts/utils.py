# -*- coding: utf-8 -*- 
import inspect, urlparse, string, datetime, csv, traceback, time, requests
from django.utils.encoding import smart_text
from getproxies import GetProxies

class Utils:
    debug              = True
    showRealTime       = True
    printToFile        = True
    logSeparator       = "_"
    @staticmethod
    def logMessage(hiper, msg, lineNo):
        if Utils.debug:
            resultFile = open("log-General-"+hiper+".csv",'a')
            wr = csv.writer(resultFile, dialect='excel')
            try:
                msg = msg.encode("utf-8")
            except:
                pass
            now = datetime.datetime.now()
            currDate = str(now.day) + "-" + str(now.month) + "-" + str(now.year)
            arrayMsg = [currDate, msg, lineNo]
            wr.writerow(arrayMsg)
            if Utils.showRealTime:
                print hiper , "-" , currDate , "-" , msg
    @staticmethod
    def logProdutos(hiper, msg):
        if Utils.debug:
            resultFile = open("log-Produtos-"+hiper+".csv",'a')
            wr = csv.writer(resultFile, dialect='excel')
            try:
                msg = msg.encode("utf-8")
            except:
                pass
            now = datetime.datetime.now()
            currDate = str(now.day) + "-" + str(now.month) + "-" + str(now.year)
            arrayMsg = [currDate] + msg.split(Utils.logSeparator)
            wr.writerow(arrayMsg)
    @staticmethod
    def getLineNo():
        #Returns the current line number in our program.
        return "["+inspect.currentframe().f_back.f_globals["__name__"]+"]-"+str(inspect.currentframe().f_back.f_lineno)
    @staticmethod
    def validUrl(url):
        try:
            pieces = urlparse.urlparse(url)
            assert all([pieces.scheme, pieces.netloc])
            assert set(pieces.netloc) <= set(string.letters + string.digits + '-.')  # and others?
            assert pieces.scheme in ['http', 'https', 'ftp']  # etc.
        except:
            return False
        return True
    @staticmethod
    def toStr(s):
        if s is None:
            return ''
        return smart_text(s, encoding='utf-8', strings_only=False, errors='strict')
    @staticmethod
    def printMsg(hiperName, msg, lineNo):
        Utils.logMessage(hiperName, msg, lineNo);
    @staticmethod
    def strip(str):
        str = str.replace("\r\n", " ")
        return" ".join(str.split())
    @staticmethod
    def saveObjToDB(obj):
        saved = False
        retries = 0
        while saved == False:
            try:
                obj.save()
                saved = True
                retries = 0
            except Exception, e:
                retries += 1
                print traceback.format_exc()
                print "ERROR %s\n\tWaiting and retrying..." % str(e)
                time.sleep(retries*5)
    @staticmethod
    def clearTables(cursor):
        cleaned = False
        retries = 0
        while cleaned == False:
            try:
                cursor.execute("set foreign_key_checks = 0")
                cursor.execute("truncate table hipers_categoria")
                cursor.execute("truncate table hipers_hiper")
                cursor.execute("truncate table hipers_produto")
                cursor.execute("set foreign_key_checks = 1")
                cleaned = True
                retries = 0
            except Exception, e:
                retries += 1
                print traceback.format_exc()
                print "ERROR %s\n\tWaiting and retrying...\n" % str(e)
                time.sleep(retries*5)
        print "Tables cleaned"
    @staticmethod
    def makeRequest(url):
        reqOK = False
        retries = 0
        while reqOK == False:
            try:
                textResp = requests.get(url).text
                if textResp == "FAILED":
                    raise Exception("Received:" + textResp)
                else:
                    reqOK = True
                    retries = 0
                    print "Received:" + textResp
            except Exception, e:
                retries += 1
                print traceback.format_exc()
                print "ERROR %s\n\tWaiting and retrying...\n" % str(e)
                time.sleep(retries*5)
        return textResp
    @staticmethod
    def makeGetRequest(session, url):
        success = False
        while (success == False):
            #proxies = GetProxies()
            #proxy = proxies.getProxy()
            #proxies = {
            #    "http": proxy,
            #}
            try:
                respTxt = session.get(url).text
                success = True
            except:
                pass
        return respTxt.replace('&nbsp;', '')
    @staticmethod
    def makePostRequest(session, url, payload):
        success = False
        while (success == False):
            #proxies = GetProxies()
            #proxy = proxies.getProxy()
            #proxies = {
            #    "http": proxy,
            #}
            try:
                respTxt = session.post(url, data=payload).text
                success = True
            except:
                pass
        return respTxt.replace('&nbsp;', '')