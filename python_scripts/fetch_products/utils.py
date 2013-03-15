import inspect, urlparse, string, datetime, csv
from django.utils.encoding import smart_text

class Utils:
    debug              = True
    showRealTime       = True
    printToFile        = True
    logSeparator       = ";"
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
