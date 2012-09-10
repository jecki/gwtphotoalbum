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


#ifndef THREADSAFESET_H
#define THREADSAFESET_H

#include <QSet>
#include <QMutex>
#include <QMutexLocker>

/*
 * Rudimentary thread safe set container.
 */
template<class T>
class ThreadSafeSet {
public:
	bool contains(const T &value) const;
	void insert(const T &value);
	void remove(const T &value);
	QSet<T> contents() const;
	bool empty() const;
	int  size() const;
	inline bool isEmpty() { return empty(); }
	void clear();

private:
	QSet<T>	set;
	mutable QMutex	mutex;
};


template<class T> bool ThreadSafeSet<T>::contains(const T &value) const {
	QMutexLocker locker(&mutex);
	return set.contains(value);
}

template<class T> void ThreadSafeSet<T>::insert(const T &value) {
	mutex.lock();
	set.insert(value);
	mutex.unlock();
}

template<class T> void ThreadSafeSet<T>::remove(const T &value) {
	mutex.lock();
	set.remove(value);
	mutex.unlock();
}

template<class T> QSet<T> ThreadSafeSet<T>::contents() const {
	QMutexLocker locker(&mutex);
	QSet<T> contents;
	foreach(T value, set) contents.insert(value);
	return contents;
}

template<class T> bool ThreadSafeSet<T>::empty() const {
	QMutexLocker locker(&mutex);
	return set.empty();
}

template<class T> int ThreadSafeSet<T>::size() const {
	QMutexLocker locker(&mutex);
	return set.size();
}

template<class T> void ThreadSafeSet<T>::clear() {
	mutex.lock();
	set.clear();
	mutex.unlock();
}



#endif // THREADSAFESET_H
