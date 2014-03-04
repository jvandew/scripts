import argparse, os

parser = argparse.ArgumentParser(description='This script removes annoying track numbers and whatnot from the names of music files in the given directory')
parser.add_argument('music_dir', help='Directory containing music files')
parser.add_argument('drop_chars', type=int, help='Number of characters to drop from the filename (eventually this will be replaced with a regex)')
parser.add_argument('-ft', default='mp3', dest='file_type', help='Extension of files to target. Default is \'mp3\'.')

args = parser.parse_args()

for filename in os.listdir(args.music_dir):
  if filename.endswith(args.file_type):
    new_name = filename[args.drop_chars:]
    print('Renaming \'' + filename + '\' to \'' + new_name + '\'.')
    os.rename(filename, new_name)

