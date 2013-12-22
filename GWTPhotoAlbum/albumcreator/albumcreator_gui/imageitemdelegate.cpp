/*
 * imageitemdelegate.cpp
 *
 *  Created on: 29.08.2012
 *      Author: eckhart
 */

#include "imageitemdelegate.h"
#include "imagelistmodel.h"
#include "iconcache.h"
#include "imageitem.h"

#include <QBrush>
#include <QPen>
#include <QColor>
#include <QPainter>
#include <QPaintEngine>
#include <QDebug>
#include <QRect>
#include <QPainter>
#include <QPixmap>
#include <QIcon>
#include <QCoreApplication>

ImageItemDelegate::ImageItemDelegate(QListView *listView, QObject *parent)
	: QStyledItemDelegate(parent), dropPosition()
{
	view = listView;
}

ImageItemDelegate::~ImageItemDelegate() {

}

void ImageItemDelegate::paint (QPainter *painter,
		const QStyleOptionViewItem &option, const QModelIndex &index) const
{
	Q_UNUSED(option);
	QStyledItemDelegate::paint(painter, option, index);
	QRect vr(view->visualRect(index));
//	qDebug() << visualRect;
//	QPixmap pixmap = (qvariant_cast<QPixmap>(index.data(Qt::DecorationRole)));
//	int offset = ImageListModel::FRAME_SIZE;
//	painter->drawPixmap(vr.x()+offset, vr.y()+offset, pixmap);

	if (index == dropPosition) {
		QBrush brush(QColor(250, 50, 50, 128)); //, Qt::LinearGradientPattern);
		QPen pen(brush, 10);
		painter->setPen(pen);
		painter->drawRect(QRect(vr.x()+5, vr.y()+5, vr.width()-10, vr.height()-10));
	}
}

void ImageItemDelegate::highlightDropPosition(const QModelIndex &index)
{
	if (index != dropPosition) {
		dropPosition = index;
		view->viewport()->update();
	}
}

void ImageItemDelegate::clearDropPosition()
{
	if (dropPosition.isValid()) {
		dropPosition = QModelIndex();
		view->viewport()->update();
	}
}
