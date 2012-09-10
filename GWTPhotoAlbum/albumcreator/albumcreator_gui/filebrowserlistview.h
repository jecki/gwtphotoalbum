/*
 * Copyright 2012 Eckhart Arnold (eckhart_arnold@hotmail.com).
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

#ifndef FILEBROWSERLISTVIEW_H
#define FILEBROWSERLISTVIEW_H

#include <QListView>
#include <QWidget>
#include <QDropEvent>


class FileBrowserListView : public QListView
{
    Q_OBJECT

public:
    FileBrowserListView(QWidget *parent = 0);
    ~FileBrowserListView();

protected:
    virtual void startDrag(Qt::DropActions supportedActions);
//    virtual void dragEnterEvent(QDragEnterEvent *event);
    //    virtual void dragLeaveEvent(QDragLeaveEvent *event);
    virtual void dragMoveEvent(QDragMoveEvent *event);
    virtual void dropEvent(QDropEvent *event);
};

#endif // FILEBROWSERLISTVIEW_H
