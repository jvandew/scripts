#!/usr/bin/env python

import argparse
from os import rename

parser = argparse.ArgumentParser(description='Change the episode number for incorrectly numbered files')
parser.add_argument('filename', help='something like \'S01E04...\'')
parser.add_argument('correct_episode', help='a new episode number for the filename')

args = parser.parse_args()
new_name = '{0}{1}{2}'.format(args.filename[:4], args.correct_episode, args.filename[6:])
print 'Renaming \'{0}\' to \'{1}\''.format(args.filename, new_name)
rename(args.filename, new_name)

