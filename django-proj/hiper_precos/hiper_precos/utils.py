from hiper_precos import settings
import os, unicodedata
from django.utils.encoding import smart_text
from os.path import expanduser

class Utils:
    lastWrittenDbFile = "lastWrittenDb.dat"
    userHome = expanduser("~")+"/"
    @staticmethod
    def getNextInArray(current, array):
        currPos = array.index(current)
        if currPos+1 >= len(array):
            return array[0]
        return array[currPos+1]
    @staticmethod
    def getAllDbs():
        databases = []
        for database in settings.DATABASES:
            if "default" in database:
                continue
            else:
                databases.append(database)
        return sorted(databases)
    @staticmethod
    def dbExists(database):
        if database in Utils.getAllDbs():
           return True
        return False
    @staticmethod
    def getDbLogFile():
        return Utils.userHome+Utils.lastWrittenDbFile
    @staticmethod
    def getDefaultDbToReadFrom():
        return Utils.getAllDbs()[0]
    @staticmethod
    def getDefaultDbToWriteTo():
        return Utils.getNextInArray(Utils.getDefaultDbToReadFrom(), Utils.getAllDbs())
    @staticmethod
    def getDbToReadFrom():
        if settings.LAST_WRITTEN_DATABASE:
            lastWrittenDb = settings.LAST_WRITTEN_DATABASE
        else:
            try:
                file = open(Utils.getDbLogFile())
            except IOError:
                # no file, no database was written yet, so use the default database to read from
                return Utils.getDefaultDbToReadFrom()
            # file exists, check last database used to write to
            file.seek(0)
            lastWrittenDb = file.readline()
            file.close()
        if (Utils.dbExists(lastWrittenDb)):
            return lastWrittenDb
        else:
            print "ERROR: Unknown written database. Reading from default database."
            Utils.getDefaultDbToReadFrom()
    @staticmethod
    def getDbToWriteTo():
        if settings.LAST_WRITTEN_DATABASE:
            lastWrittenDb = settings.LAST_WRITTEN_DATABASE
        else:
            try:
                file = open(Utils.getDbLogFile())
            except IOError:
                # no file, no database was written yet, so use the default database to write to
                return Utils.getDefaultDbToWriteTo()
            # file exists, check last database used to write to
            file.seek(0)
            lastWrittenDb = file.readline()
            file.close()
        if (Utils.dbExists(lastWrittenDb)):
            return Utils.getNextInArray(lastWrittenDb, Utils.getAllDbs())
    @staticmethod
    def dbPopulated():
        currDbToWrite = Utils.getDbToWriteTo()
        # we were writing to one database, confirm the completion by writing it in the file
        file = open(Utils.getDbLogFile(), "w")
        file.write(currDbToWrite);
        file.close()
    @staticmethod
    def strip_accents(s):
        return ''.join(c for c in unicodedata.normalize('NFD', s) if unicodedata.category(c) != 'Mn')
    @staticmethod
    def toStr(s):
        if s is None:
            return ''
        return smart_text(s, encoding='utf-8', strings_only=False, errors='strict')