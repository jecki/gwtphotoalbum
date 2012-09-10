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


#include <kipiplugin_webslideshow.h>

// Qt
#include <QPointer>
#include <QUrl>
#include <QString>
#include <QDesktopServices>

// KDE
#include <kaction.h>
#include <kactioncollection.h>
#include <kapplication.h>
#include <kgenericfactory.h>
#include <klibloader.h>
#include <klocale.h>
#include <kmessagebox.h>
#include <krun.h>

// KIPIPlugins
#include <libkipi/imagecollection.h>
#include <libkipi/interface.h>

// albumcreatos
#include <imageitem.h>
#include <imagecollection.h>
#include <createalbumwizzard.h>


K_PLUGIN_FACTORY(WebGalleryFactory, registerPlugin<Plugin_WebSlideShow>();)
K_EXPORT_PLUGIN( WebGalleryFactory("kipiplugin_webslideshow"))



Plugin_WebSlideShow::Plugin_WebSlideShow(QObject *parent, const QVariantList&)
: KIPI::Plugin(WebGalleryFactory::componentData(), parent, "WebSlideshow")
{
}


Plugin_WebSlideShow::~Plugin_WebSlideShow() {
}


void Plugin_WebSlideShow::setup(QWidget* widget) {
    KIPI::Plugin::setup(widget);

    m_action = actionCollection()->addAction("webslideshow");
    m_action->setText(i18n("Create a Web-&Slideshow..."));
    m_action->setIcon(KIcon("applications-internet"));
    m_action->setShortcut(KShortcut(Qt::ALT+Qt::SHIFT+Qt::Key_S));
    connect(m_action, SIGNAL(triggered()), this, SLOT(slotActivate()));
    addAction(m_action);

    m_iface = dynamic_cast<KIPI::Interface*>(parent());
    if (!m_iface) {
    	kError() << "Kipi interface is null!";
    	return;
    }

    albumOrSelectionChanged(true);
    connect(m_iface, SIGNAL(selectionChanged(bool)),
    		this, SLOT(albumOrSelectionChanged(bool)));
    connect(m_iface, SIGNAL(currentAlbumChanged(bool)),
    		this, SLOT(albumOrSelectionChanged(bool)));
}


KIPI::Category Plugin_WebSlideShow::category(KAction* action) const {
    if (action == m_action) {
        return KIPI::ExportPlugin;
    }

    kWarning() << "Unrecognized action for plugin category identification";
    return KIPI::ExportPlugin; // no warning from compiler, please
}


void Plugin_WebSlideShow::slotActivate() {
	KIPI::ImageCollection selection = m_iface->currentSelection();
	if ( !selection.isValid() || selection.images().isEmpty() ) {
		selection = m_iface->currentAlbum();
	}

	if (!selection.isValid() || selection.images().isEmpty()) {
		KMessageBox::error(0, i18n("No images or album selected!"));
		return;
	}

	ImageCollection	ic;
	ic.title = selection.name();
	ic.subtitle = selection.comment() + "<br />" + selection.date().toString();
	ic.bottom_line = "created with <a href=\"http://code.google.com/p/gwtphotoalbum/\">GWTPhotoAlbum</a>";

	foreach(KUrl url, selection.images()) {
		if (url.isLocalFile()) {
			ImageItem *it = new ImageItem(url.path());
			ic.imageList.append(it);
		}
	}

	if (ic.imageList.isEmpty()) {
		KMessageBox::error(0, i18n("Webslideshow Plugin can only process local files!"));
		return;
	}

	if (ic.imageList.size() == 1) {
		if (!(KMessageBox::questionYesNoCancel(0, i18n("Only 1 image selected! Do you really want to proceed?"))
		      == KMessageBox::Yes)) return;
	}

	CreateAlbumWizzard wizzard(ic);
	wizzard.setAboutTab(true);
	wizzard.setChangesLostDialog(false);
	wizzard.exec();
}


void Plugin_WebSlideShow::albumOrSelectionChanged(bool hasSelection) {
	(void) hasSelection;
    KIPI::ImageCollection selection = m_iface->currentSelection();
    KIPI::ImageCollection album = m_iface->currentAlbum();
    m_action->setEnabled((selection.isValid() && !selection.images().isEmpty())
    		|| (album.isValid() && !album.images().isEmpty()) );
}


