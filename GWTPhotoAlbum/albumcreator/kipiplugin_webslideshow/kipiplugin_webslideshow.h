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

#ifndef KIPI_WEBSLIDESHOW_PLUGIN_H
#define KIPI_WEBSLIDESHOW_PLUGIN_H

#include <QVariant>
#include <libkipi/plugin.h>

namespace KIPI{
	class Interface;
}


/**
 * KIPI::Plugin class for generating a GWTPhotoAlbum
 */
class Plugin_WebSlideShow : public KIPI::Plugin {
	Q_OBJECT

public:
	Plugin_WebSlideShow(QObject *parent, const QVariantList &args);
	virtual ~Plugin_WebSlideShow();

	KIPI::Category category(KAction* action) const;
	virtual void setup(QWidget* widget);

private Q_SLOTS:
	void slotActivate();
	void albumOrSelectionChanged(bool hasSelection);

private:
	KAction*			m_action;
	KIPI::Interface*	m_iface;
};




#endif // KIPI_WEBSLIDESHOW_PLUGIN_H
