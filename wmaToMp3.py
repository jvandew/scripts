import argparse, os, sys
from multiprocessing import Pool
from pipes import quote

def append_path(path1, path2):
  return path1 + os.sep + path2

def process(base_path):
  cmd = 'ffmpeg -i ' + base_path + '.wma -q:a 0 ' + base_path + '.mp3'
  sys.stderr.write(cmd + '\n')
  os.system(cmd)

  # this does not seem to be working... why?
  os.remove(base_path + '.wma')

parser = argparse.ArgumentParser(description='Convert .wma audio files to the .mp3 format using ffmpeg; assumes a music directory organized by artist')
parser.add_argument('music_dir', help='Directory containing music files')
parser.add_argument('-t', default=1, type=int, dest='threads', help='Use the given number of threads during conversion')

args = parser.parse_args()
pool = Pool(args.threads)

for dir in os.listdir(args.music_dir):
  artist_dir = append_path(args.music_dir, dir)

  if os.path.isdir(artist_dir):
    for file in os.listdir(artist_dir):
      (base, ext) = os.path.splitext(file)
      
      if ext == '.wma':
        base_path = quote(append_path(artist_dir, base))
        pool.apply_async(process, [base_path])

pool.close()
pool.join()

