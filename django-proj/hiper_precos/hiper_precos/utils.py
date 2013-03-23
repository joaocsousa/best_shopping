from hiper_precos import settings
import os

class Utils:
    lastDbFile = "lastWrittenDb.dat"
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
        return os.getenv("HOME")+"/"+Utils.lastDbFile
    @staticmethod
    def getDefaultDbToReadFrom():
        return Utils.getAllDbs()[0]
    @staticmethod
    def getDefaultDbToWriteTo():
        return Utils.getNextInArray(Utils.getDefaultDbToReadFrom(), Utils.getAllDbs())
    @staticmethod
    def getDbToReadFrom():
        try:
            file = open(Utils.getDbLogFile())
        except IOError:
            # no file, no database was written yet, so use the default database to read from
            return Utils.getDefaultDbToReadFrom()
        # file exists, check last database used to write to
        file.seek(0)
        lastDb = file.readline()
        if (Utils.dbExists(lastDb)):
            return lastDb
        else:
            print "ERROR: Unknown written database. Reading from default database."
            Utils.getDefaultDbToReadFrom()
        file.close()
    @staticmethod
    def getDbToWriteTo():
        try:
            file = open(Utils.getDbLogFile())
        except IOError:
            # no file, no database was written yet, so use the default database to write to
            return Utils.getDefaultDbToWriteTo()
        # file exists, check last database used to write to
        file.seek(0)
        lastDb = file.readline()
        if (Utils.dbExists(lastDb)):
            return Utils.getNextInArray(lastDb, Utils.getAllDbs())
        file.close()
    @staticmethod
    def dbPopulated():
        currDbToWrite = Utils.getDbToWriteTo()
        # we were writing to one database, confirm the completion by writing it in the file
        file = open(Utils.getDbLogFile(), "w")
        file.write(currDbToWrite);
        file.close()