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


#include <QCoreApplication>
#include <QtTest/QtTest>

#include "test_jsonsupport.h"
#include "test_htmlprocessing.h"
#include "test_imageitem.h"
#include "test_imagecollection.h"
#include "test_zipfile.h"


int run_unit_tests(int argc, char** argv) {
	QCoreApplication a(argc, argv);

	int ret = 0;

	test_jsonsupport 	 t1;
	test_htmlprocessing  t2;
	test_imageitem		 t3;
	test_imagecollection t4;
	test_zipfile         t5;

	ret += QTest::qExec(&t1, argc, argv);
	ret += QTest::qExec(&t2, argc, argv);
    ret += QTest::qExec(&t3, argc, argv);
    ret += QTest::qExec(&t4, argc, argv);
    ret += QTest::qExec(&t5, argc, argv);

	return (ret ? 1 : 0);
}


int main(int argc, char *argv[])
{
    return run_unit_tests(argc, argv);

}
