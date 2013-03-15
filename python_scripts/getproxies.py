#!/home/tinycool/.env/bin/python2.7

###########################################
__app__ = "GetProxy"
__version__ = "0.4"
__author__ = "MatToufoutu"
# Inspired from darkc0de's ProxyHarvest
###########################################
#TODO: write elite list to file periodically
#TODO: try to limit the proxy amount to check, with equivalent results

import socket, re, urllib2, httplib
from os import system, getcwd, remove
from sys import exit as sysexit
import requests
from bs4 import BeautifulSoup
import time

timeout = 5
proxyfile = "proxylist"

socket.setdefaulttimeout(timeout)
regex_ip = re.compile(r"(?:\d{1,3}\.){3}\d{1,3}")
regex_proxy = re.compile(r"(?:\d{1,3}\.){3}\d{1,3}:\d{1,5}")

def write2file(proxy):
    ofile = open(proxyfile, 'a')
    ofile.write(proxy+'\n')
    ofile.close()

class GetProxies:
    """Get/sort/check proxies collected from various websites"""
    def __init__(self):
        self.trueIP = self.getTrueIP()
        print("[+] True IP address: %s"% self.trueIP)
        self.codeenlist = []
        self.elitelist = []
        self.anonlist = []
        self.transplist = []
        self.otherlist = []

    def getTrueIP(self):
        """Get the machine's true IP address"""
        opener = urllib2.build_opener()
        opener.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11')]
        soup = BeautifulSoup(opener.open('http://whatismyipaddress.com/').read())
        ip = soup.find("span", {"class": "ip blue"}).text.replace("do not script","")
        return ip.strip()

    def check(self, proxy):
        """Check if <proxy> is anonymous"""
        re_pxtype = re.compile(r'You\s*are\s*using\s*(?:<b>)?<font\s*color="\#[a-f0-9]{6}">\s*(?:\s*<b>)?(.+\s*proxy)(?:</b>)?</font>')
        re_ip = re.compile(r'IP\s*detected:\s*<font\s*color="\#[a-f0-9]{6}">\s*((\d{1,3}\.){3}\d{1,3})')
        handler = urllib2.ProxyHandler({'http':proxy})
        opener = urllib2.build_opener(handler)
        opener.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11')]
        wrote = False
        try:
            html = opener.open('http://checker.samair.ru/').read()
            result = re.search(re_pxtype, html)
            if result:
                pxtype = result.groups()[0]
                ip = re.search(re_ip, html).groups()[0]
                if pxtype == 'high-anonymous (elite) proxy':
                    if ip == self.trueIP:
                        if __name__ == "__main__":
                            print(" - %s > TRANSPARENT" % proxy)
                        self.transplist.append(proxy)
                    else:
                        if __name__ == "__main__":
                            print(" - %s > ELITE" % proxy)
                        self.elitelist.append(proxy)
                        write2file(proxy)
                        wrote = True
                elif pxtype == 'anonymous proxy':
                    if __name__ == "__main__":
                        print(" - %s > ANONYMOUS" % proxy)
                    self.anonlist.append(proxy)
                else:
                    if "CoDeeN" in html:
                        if __name__ == "__main__":
                            print(" - %s > CODEEN" % proxy)
                        self.codeenlist.append(proxy.split(":")[0])
                    else:
                        if __name__ == "__main__":
                            print(" - %s > UNDEFINED" % proxy)
                        self.otherlist.append(proxy)
            opener.close()
            return wrote
        except KeyboardInterrupt:
            opener.close()
            sysexit(0)
        except:
            opener.close()
            return wrote
            pass


    def get_Codeen(self, retried=False):
        """Get a list of CoDeeN proxies"""
        try:
            if __name__ == "__main__":
                print(" - Gathering a list of known CoDeeN proxies...")
            opener = urllib2.build_opener()
            html = opener.open('http://proxy.org/planetlab.shtml')
            proxies = re.findall(regex_ip, html)
            for proxy in proxies:
                self.codeenlist.append(proxy)
            opener.close()
            return self.codeenlist
        except (urllib2.URLError, socket.timeout):
            if retried:
                print(" - Site unavailable: Aborting")
                opener.close()
                return []
            else:
                print(" - Site unavailable: Retrying")
                opener.close()
                self.get_Codeen(True)

    def getfrom_Multi(self):
        """Get a list from various websites providing small proxy lists"""
        sites = []
        proxylist = []
        opener = urllib2.build_opener()
        opener.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11')]
        for site in sites:
            try:
                if __name__ == "__main__":
                    print(" - Gathering proxy list from %s" % site)
                html = opener.open(site).read()
                proxylist.extend(re.findall(regex_proxy, html))
            except (urllib2.URLError, socket.timeout):
                print(" - Site unavailable: Aborting")
                pass
        opener.close()
        return proxylist

    def getfrom_Ipadress(self, retried=False):
        """Get a proxy list from http://www.ip-adress.com/"""
        if __name__ == "__main__":
            print(" - Gathering proxy list from http://www.ip-adress.com/proxy_list/")
        proxylist = []
        opener = urllib2.build_opener()
        opener.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11')]
        try:
            html = opener.open('http://www.ip-adress.com/proxy_list/').read()
            proxylist.extend(re.findall(regex_proxy, html))
            opener.close()
            return proxylist
        except (urllib2.URLError, socket.timeout):
            if retried:
                print(" - Site unavailable: Aborting")
                opener.close()
                return []
            else:
                print(" - Site unavailable: Retrying")
                opener.close()
                self.getfrom_Ipadress(True)

    def getfrom_Samair(self, retried=False):
        """Get a proxy list from http://samair.ru/"""
        if __name__ == "__main__":
            print(" - Gathering proxy list from http://www.samair.ru/proxy/")
        token, maxpages = 1, 20
        proxylist = []
        try:
            while token <= maxpages:
                session = requests.Session()
                if token < 10:
                    html = session.get('http://www.samair.ru/proxy/proxy-0%d.htm' % token).text
                else:
                    html = session.get('http://www.samair.ru/proxy/proxy-%d.htm' % token).text
                proxylist.extend(re.findall(regex_proxy, html))
                token += 1
            return proxylist
        except (urllib2.URLError, socket.timeout, AttributeError):
            if retried:
                print(" - Site unavailable: Aborting")
                return []
            else:
                print(" - Site unavailable: Retrying")
                self.getfrom_Samair(True)

    def getfrom_Proxylist(self, retried=False):
        """Get a proxy list from http://www.proxylist.net"""
        if __name__ == "__main__":
            print(" - Gathering proxy list from http://www.proxylist.net")
        baseurl = "http://www.proxylist.net/list/0/0/3/0/"
        token, maxpages = 0, 15
        proxylist = []
        try:
            while token <= maxpages:
                opener = urllib2.build_opener()
                opener.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.5) Gecko/20091106 Shiretoko/3.5.5')]
                html = opener.open(baseurl+str(token)).read()
                proxylist.extend(re.findall(regex_proxy, html))
                token += 1
                opener.close()
            return proxylist
        except (urllib2.URLError, socket.timeout):
            if retried:
                print(" - Site unavailable: Aborting")
                opener.close()
                return []
            else:
                print(" - Site unavailable: Retrying")
                opener.close()
                self.getfrom_Proxylist(True)

    def getAllProxies(self):
        """Get a proxy list from all possible websites"""
        getfuncs = [
                    self.getfrom_Multi,
                    self.getfrom_Ipadress,
                    self.getfrom_Samair,
                    self.getfrom_Proxylist
                    ]
        proxylist = []
        codeenlist = self.get_Codeen()
        codeenlist = []
        for get_proxy in getfuncs:
            try:
                proxylist.extend(get_proxy())
            except TypeError:
                pass
        return proxylist


if __name__ == "__main__":
    try:
        start_time = time.time()
        try:
            remove(proxyfile)
        except:
	    pass
     	system('clear')
        print(76*"#")
        print("\t\t\t[ GetProxy List Maker v%s ]\n" % __version__)
        print(76*"#"+"\n")
        collector = GetProxies()
        print("[+] Downloading proxy lists")
        proxylist1 = collector.getAllProxies()
        proxylist = []
        for proxy in proxylist1:
            if proxy not in proxylist:
                proxylist.append(proxy)
        for proxy in proxylist:
            if proxy in collector.codeenlist:
                proxylist.remove(proxy)
        print " - Total proxies: %d\n[+] Checking proxies" , len(proxylist)
        contador = 0
        for proxy in proxylist:
            if (collector.check(proxy)):
                contador += 1
            if contador == 5:
                print " - We have %d proxies. No need for more." , contador
                break
        print "[+] FINISHED !"
        print " - Total elite proxies: " , len(collector.elitelist)
        print " - List written to " , getcwd() , "/", proxyfile
        print "Finished - Elapsed: " + str(time.time()-start_time) + " seconds"
        sysexit(0)
    except KeyboardInterrupt:
        remove(getcwd()+"/"+proxyfile)
        sysexit(0)
