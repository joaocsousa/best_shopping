from hiper_precos.utils import Utils

class DBRouter(object):
    """
    A router to control all database operations on models in the
    auth application.
    """
    def db_for_read(self, model, **hints):
        """
        Attempts to read auth models go to auth_db.
        """
        return Utils.getDbToReadFrom()

    def db_for_write(self, model, **hints):
        """
        Attempts to write auth models go to auth_db.
        """
        return Utils.getDbToWriteTo()

    def allow_relation(self, obj1, obj2, **hints):
        """
        Allow relations if a model in the auth app is involved.
        """
        return None

    def allow_syncdb(self, db, model):
        """
        Make sure the auth app only appears in the 'auth_db'
        database.
        """
        return None