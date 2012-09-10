/*
 * imageitemdelegate.cpp
 *
 *  Created on: 29.08.2012
 *      Author: eckhart
 */

#include "imageitemdelegate.h"
#include <QBrush>
#include <QPen>
#include <QColor>
#include <QPainter>
#include <QDebug>


ImageItemDelegate::ImageItemDelegate(QObject *parent)
	: QStyledItemDelegate(parent), dropPosition()
{

}

ImageItemDelegate::~ImageItemDelegate() {

}


void ImageItemDelegate::paint (QPainter *painter,
		const QStyleOptionViewItem &option, const QModelIndex &index) const
{
	QStyledItemDelegate::paint(painter, option, index);
	if (index == dropPosition) {
		qDebug() << painter->viewport();
		QBrush brush(QColor(250, 50, 50, 128), Qt::LinearGradientPattern);
		QPen pen(brush, 20);
		painter->setPen(pen);
		painter->drawRect(painter->viewport());
	}
}

void ImageItemDelegate::highlightDropPosition(const QModelIndex &index)
{

	dropPosition = index;
}

void ImageItemDelegate::clearDropPosition()
{
	dropPosition = QModelIndex();
}
