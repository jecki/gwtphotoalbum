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

#ifndef FILEJOURNAL_H
#define FILEJOURNAL_H

#include <QtCore>
#include <QString>
#include <QSet>

class FileJournal : public QObject
{
    Q_OBJECT

public:
    FileJournal(const QString &rootDir, QObject *parent = 0);
    ~FileJournal();

    void registerFile(const QString &path);
    void registerDir(const QString &path);

    void deregisterFile(const QString &path);
    void deregisterDir(const QString &path);

    bool rollback(const QString &path);

    bool	hasErrors() { return !error.isEmpty(); }
    QString debugMsg() { return error; };

protected:
    QString	      head(const QString &path);
    QString		  tail(const QString &path);
    bool		  isDescendant(const QString &item, const QString &parent);
    bool 		  isDirectDescendant(const QString &item, const QString &parent);
    bool		  hasDescendants(const QString &dir);
    QSet<QString> findDescendantDirs(const QString &dir);
    QSet<QString> findDescendantFiles(const QString &dir);
    bool		  hasDirectParent(const QString &item, const QSet<QString> &dirs);

private:
    QSet<QString> files;
    QSet<QString> directories;

    QString		  error;

};

#endif // FILEJOURNAL_H
