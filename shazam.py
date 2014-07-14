# The script serves to aggregate a list of Shazam song tags by artist and
# song. Song tags are expected to be provided in the html format provided
# on the Shazam app website.

import codecs
from copy import deepcopy
from HTMLParser import HTMLParser
from operator import itemgetter
from os.path import isfile, join

class SongTableParser(HTMLParser):

  __fieldNum__ = 0
  __tableData__ = False
  __tableRow__ = False

  __curSong__ = ''
  __curArtist__ = ''

  songDict = {}

  def handle_starttag(self, tag, attrs):
    if tag == 'td' and not self.__tableData__:
      self.__tableData__ = True
    elif tag == 'tr' and not self.__tableRow__:
      self.__tableRow__ = True

  def handle_endtag(self, tag):
    if tag == 'td' and self.__tableData__:
      self.__tableData__ = False
    elif tag == 'tr' and self.__tableRow__:
      self.__tableRow__ = False

  def handle_data(self, data):
    if self.__tableRow__ and self.__tableData__:
      if self.__fieldNum__ is 0:
        self.__curSong__ = data
        self.__fieldNum__ += 1

      elif self.__fieldNum__ is 1:
        self.__curArtist__ += data
        self.__fieldNum__ += 1

      elif self.__fieldNum__ is 2:
        if not self.__curArtist__ in self.songDict:
          self.songDict[self.__curArtist__] = {self.__curSong__: 1}
        else:
          if not self.__curSong__ in self.songDict[self.__curArtist__]:
            self.songDict[self.__curArtist__][self.__curSong__] = 1
          else:
            self.songDict[self.__curArtist__][self.__curSong__] += 1

        self.__curArtist__ = ''
        self.__fieldNum__ = 0

      elif self.__fieldNum__ is 2:
        self.__fieldNum__ = 0

  def handle_entityref(self, name):
    char = ''

    if name == 'amp':
      char = '&'
    elif name == 'quot':
      char = '"'
    else:
      print 'Unknown entityref: \'%s\'' % name

    if self.__fieldNum__ <= 1:
      self.__curSong__ += char
    elif (self.__fieldNum__ is 1 and self.__curArtist__ == '') or self.__fieldNum__ is 2:
      self.__curArtist__ += char

    self.__fieldNum__ -= 1


# Filter out songs from Shazam history that are already present in the music
# library. Necessary due to Shazam's broken website refusing to delete tags.
def filterSongDict(dic, musicDir):
  copy = deepcopy(dic)

  for artist in dic:
    for song in dic[artist]:
      if (isfile(join(musicDir, artist, song + '.mp3')) and
          artist in copy and song in copy[artist]):
        del copy[artist][song]
        if not copy[artist]:
          del copy[artist]

  return copy

def printSongDict(dic):
  for artist in sortArtists(dic):
    print artist.encode('utf_8')
    for song in dic[artist]:
      print ('\t\t%s : %d' % (song, dic[artist][song])).encode('utf_8')

# returns a list of artists sorted by number of shazams
def sortArtists(dic):
  sortDic = sorted(dic.iteritems(), key=lambda x: sum(itemgetter(1)(x).itervalues()), reverse=True)
  return map(itemgetter(0), sortDic)


# script starts here
parser = SongTableParser()
html = codecs.open('myshazam-history.html', encoding='utf-8')
parser.feed(html.read())

musicDir = '/Users/jacob/Music'

filteredDict = filterSongDict(parser.songDict, musicDir)
printSongDict(filteredDict)

html.close()

