if [ "$#" -le 1 ]; then
	exit 1
fi

$SPARK_HOME/sbin/start-master.sh $@