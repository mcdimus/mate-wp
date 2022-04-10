docker run --rm -it -p 8080:8080 \
  -v `pwd`/../:/data/project/ \
  -v `pwd`/../build/:/data/results/ \
jetbrains/qodana-jvm-community --show-report

