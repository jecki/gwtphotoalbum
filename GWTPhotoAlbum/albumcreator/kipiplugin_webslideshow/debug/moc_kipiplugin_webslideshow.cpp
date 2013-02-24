/****************************************************************************
** Meta object code from reading C++ file 'kipiplugin_webslideshow.h'
**
** Created: Sun Jan 27 22:31:41 2013
**      by: The Qt Meta Object Compiler version 63 (Qt 4.8.4)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../kipiplugin_webslideshow.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'kipiplugin_webslideshow.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 63
#error "This file was generated using the moc from 4.8.4. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_Plugin_WebSlideShow[] = {

 // content:
       6,       // revision
       0,       // classname
       0,    0, // classinfo
       2,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: signature, parameters, type, tag, flags
      21,   20,   20,   20, 0x08,
      49,   36,   20,   20, 0x08,

       0        // eod
};

static const char qt_meta_stringdata_Plugin_WebSlideShow[] = {
    "Plugin_WebSlideShow\0\0slotActivate()\0"
    "hasSelection\0albumOrSelectionChanged(bool)\0"
};

void Plugin_WebSlideShow::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        Q_ASSERT(staticMetaObject.cast(_o));
        Plugin_WebSlideShow *_t = static_cast<Plugin_WebSlideShow *>(_o);
        switch (_id) {
        case 0: _t->slotActivate(); break;
        case 1: _t->albumOrSelectionChanged((*reinterpret_cast< bool(*)>(_a[1]))); break;
        default: ;
        }
    }
}

const QMetaObjectExtraData Plugin_WebSlideShow::staticMetaObjectExtraData = {
    0,  qt_static_metacall 
};

const QMetaObject Plugin_WebSlideShow::staticMetaObject = {
    { &KIPI::Plugin::staticMetaObject, qt_meta_stringdata_Plugin_WebSlideShow,
      qt_meta_data_Plugin_WebSlideShow, &staticMetaObjectExtraData }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &Plugin_WebSlideShow::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *Plugin_WebSlideShow::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *Plugin_WebSlideShow::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_Plugin_WebSlideShow))
        return static_cast<void*>(const_cast< Plugin_WebSlideShow*>(this));
    typedef KIPI::Plugin QMocSuperClass;
    return QMocSuperClass::qt_metacast(_clname);
}

int Plugin_WebSlideShow::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    typedef KIPI::Plugin QMocSuperClass;
    _id = QMocSuperClass::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 2)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 2;
    }
    return _id;
}
QT_END_MOC_NAMESPACE
