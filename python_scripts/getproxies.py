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
import random

timeout = 5
socket.setdefaulttimeout(timeout)
regex_ip = re.compile(r"(?:\d{1,3}\.){3}\d{1,3}")
regex_proxy = re.compile(r"(?:\d{1,3}\.){3}\d{1,3}:\d{1,5}")

class GetProxies:

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
        try:
            html = opener.open('http://checker.samair.ru/').read()
            result = re.search(re_pxtype, html)
            if result:
                pxtype = result.groups()[0]
                ip = re.search(re_ip, html).groups()[0]
                if pxtype == 'high-anonymous (elite) proxy':
                    if ip != self.trueIP:
                        return proxy
            opener.close()
        except:
            opener.close()
            pass
        return None

    def getfrom_Ipadress(self, retried=False):
        """Get a proxy list from http://www.ip-adress.com/"""
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

    def getAllProxies(self):
        """Get a proxy list from all possible websites"""
        getfuncs = [self.getfrom_Ipadress]
        proxylist = []
        for get_proxy in getfuncs:
            try:
                proxylist.extend(get_proxy())
            except TypeError:
                pass
        return proxylist

    def getProxy(self):
        self.trueIP = self.getTrueIP()
        proxylist1 = self.getAllProxies()
        proxylist = []
        for proxy in proxylist1:
            if proxy not in proxylist:
                proxylist.append(proxy)
        random.shuffle(proxylist)
        proxyToRtrn = None
        for proxy in proxylist:
            if (self.check(proxy)):
                proxyToRtrn = proxy
                break
        return proxyToRtrn