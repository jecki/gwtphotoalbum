#!/bin/bash

ant build
cd scripts
python genDeploymentPackage.py
cd ..
cp VERSION.txt albumcreator/common/data
cd albumcreator
qmake
make debug

#cd scripts
#quick_test.sh
#cd ..

bash createTarBalls.sh
bash createKipiTarBall.sh

