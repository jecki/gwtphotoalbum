/*
 * Copyright 2011 Eckhart Arnold (eckhart_arnold@hotmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

#include "filejournal.h"

FileJournal::FileJournal(const QString &rootDir, QObject *parent)
    : QObject(parent)
{
	this->directories.insert(rootDir);
}

FileJournal::~FileJournal()
{

}


/*!
 * Adds a path to a file to the journal.
 * The file will not be added to the journal and a debug message set,
 * if the parent directory is not in the journal or if the file has
 * already been added.
 *
 * @param path  file path to be added.
 */
void FileJournal::registerFile(const QString &path)
{
	Q_ASSERT_X(QFileInfo(path).isFile(), "FileJournal::registerFile", "not a file!");

	if (hasDirectParent(path, directories)) {
		if (!files.contains(path)) {
			files.insert(path);
			return;
		} else {
			error = "file " + path + " already in journal";
		}
	} else {
		error = "parent dir to " + path + "must be added first";
	}

	Q_ASSERT_X(hasErrors(), "FileJournal::registerFile", error.toAscii().data());
}


/*!
 * Adds a path to a directory to the journal.
 * The path will not be added to the journal and a debug message set,
 * if the parent directory is not in the journal or if the path has
 * already been added.
 * @param path  directory path to be added.
 */
void FileJournal::registerDir(const QString &path)
{
	Q_ASSERT_X(QFileInfo(path).isDir(), "FileJournal::registerDir", "not a directory!");

	if (hasDirectParent(path, directories)) {
		if (!files.contains(path)) {
			directories.insert(path);
			return;
		} else {
			error = "dir " + path + " already in journal";
		}
	} else {
		error = "parent dir to " + path + "must be added first";
	}

	Q_ASSERT_X(hasErrors(), "FileJournal::registerDir", error.toAscii().data());
}


/*!
 * Removes a file path from the journal.
 * A debug message will be set if the path was not in the journal.
 *
 * @param path  file path to be removed.
 */
void FileJournal::deregisterFile(const QString &path)
{
	if (files.contains(path)) {
		files.remove(path);
		return;
	} else {
		error = "file " + path + " was not in the journal";
		Q_ASSERT_X(hasErrors(), "FileJournal::deregisterFile", error.toAscii().data());
	}
}


/*!
 * Removes a directory path from the journal.
 * The directory will not be removed from the journal if the journal still
 * contains descendants of the directory.
 * A debug message will also be set if the path was not in the journal at all.
 *
 * @param path  file path to be removed.
 */
void FileJournal::deregisterDir(const QString &path)
{
	if (directories.contains(path)) {
		if  (!hasDescendants(path)) {
			directories.remove(path);
		} else {
			error = "directory "+path+" still has entries in the journal";
		}
	} else {
		error = "directory " + path + " was not in the journal";
	}

	Q_ASSERT_X(hasErrors(), "FileJournal::deregisterDir", error.toAscii().data());
}


/*!
 * Deletes a file or a directory and all its descendants in the file system
 * and removes the corresponding entries from the journal.
 * Only files and directories found in the journal will be deleted!
 * Directories and sub directories will only be deleted and removed from the
 * file system, if they are not empty in the file system, notwithstanding their
 * status in the journal.
 *
 * @param  path to the file or directory tree to be deleted.
 * @return true, if all files and directories could be deleted; false otherwise
 */
bool FileJournal::rollback(const QString &path)
{
	if (directories.contains(path)) {
		QSet<QString> descDirs = findDescendantDirs(path);
		QSet<QString> descFiles = findDescendantFiles(path);

		// remove descFiles

		// remove descDirs

		directories.remove(path);
		// delete directory

	} else if (files.contains(path)) {
		QFile file(path);
		files.remove(path);
		if (file.remove()) {
			return true;
		} else {
			error = file.errorString();
			return false;
		}
	} else {
		error = "path " + path + " was not in the journal";
	}

	Q_ASSERT_X(hasErrors(), "FileJournal::rollback", error.toAscii().data());

	return false;
}

