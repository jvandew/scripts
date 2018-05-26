import argparse
from os import listdir, rename
from os.path import join

parser = argparse.ArgumentParser(description='This script removes annoying track numbers and whatnot from the names of music files in the given directory')
parser.add_argument('music_dir', help='Directory containing files')
parser.add_argument('drop_chars', type=int, help='Number of characters to drop from the filename (eventually this will be replaced with a regex)')
parser.add_argument('end_index', type=int, nargs='?', default=None, help='optionally take a slice instead of just dropping characters')
parser.add_argument('-replace', default='', help='Optionally replace dropped characters with a new prefix')
parser.add_argument('-ft', default='mp3', dest='file_type', help='Extension of files to target. Default is \'mp3\'')

args = parser.parse_args()

renames = []

for filename in listdir(args.music_dir):
  if filename.endswith(args.file_type):
    new_name = '{0}{1}.{2}'.format(args.replace, filename[args.drop_chars:args.end_index], args.file_type)
    renames.append((filename, new_name))
    print('Renaming \'{0}\' to \'{1}\'.'.format(filename, new_name))

print('')
cont = raw_input('Continue? [y/n]: ').lower()

if cont == 'y':
  for filename, new_name in renames:
    rename(join(args.music_dir, filename), join(args.music_dir, new_name))

