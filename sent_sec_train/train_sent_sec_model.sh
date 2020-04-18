#!/bin/bash

# compile java package
# mvn clean compile assembly:single

rootdir="/Users/xinyue/eclipse-workspace/sema4-nlp/"

sec_train="$rootdir/sent-sec-data/bio/train/"
sec_dev="$rootdir/sent-sec-data/bio/dev/"
sec_test="$rootdir/sent-sec-data/bio/test/"

anndir="$rootdir/sent-sec-data/anndir/"

model="$rootdir/sent-sec-data/model"

Sema4Jar="$rootdir/target/trainSentSecPreprocessing-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

count_train=`ls $sec_train | wc -w`
if [ $count_train == 0 ]; then
#1. Each file generate bio
	echo "Start generate bio files"
	java -cp $Sema4Jar com.melax.contract.trainSentSecPreprocessing.GenBio $anndir $sec_train
else
	echo "bio files have generated"
fi

count_test=`ls $sec_test | wc -w`
count_dev=`ls $sec_dev | wc -w`
if [ $count_test == 0 ] && [ $count_dev == 0 ]; then
# 2. Split corpus
	echo "Start split corpus into dev/test/train"
	java -cp $Sema4Jar com.melax.contract.trainSentSecPreprocessing.SplitCorpus $sec_train $sec_test $sec_dev

else
	echo "Corpus splited"
fi

echo "Generate dev/test/train bio file"

java -cp $Sema4Jar com.melax.contract.trainSentSecPreprocessing.MergeBIO $model/input/dev.txt $sec_dev
java -cp $Sema4Jar com.melax.contract.trainSentSecPreprocessing.MergeBIO $model/input/test.txt $sec_test
java -cp $Sema4Jar com.melax.contract.trainSentSecPreprocessing.MergeBIO $model/input/train.txt $sec_train



#3. Start to train rnn model
# CPU docekr command
echo "Start to train rnn model"
# docker load -i clamp-rnn-trainer-cpu-sema4.tar

# training parameters
lower=1                 # Lowercase words (this will not affect character inputs)
zeros=1                 # Replace digits with 0
char_dim=25             # Char embedding dimension
char_lstm_dim=25        # Char LSTM hidden layer size
char_bidirect=1         # Use a bidirectional LSTM for chars
word_dim=200            # Token embedding dimension
word_lstm_dim=100       # Token LSTM hidden layer size
word_bidirect=1         # Use a bidirectional LSTM for words
crf=1                   # Use CRF (0 to disable)
dropout='0.5'           # Dropout on the input (0 = no dropout)
tag_scheme='iob'        # Tagging scheme (IOB or IOBES) 

docker run  -e lower=$lower -e zeros=$zeros -e char_dim=$char_dim -e char_lstm_dim=$char_lstm_dim -e char_bidirect=$char_bidirect -e word_dim=$word_dim -e word_lstm_dim=$word_lstm_dim -e word_bidirect=$word_bidirect -e crf=$crf -e dropout=$dropout -e tag_scheme=$tag_scheme -v $model/:/data/ clamp-rnn-trainer-cpu:bio /app/train_entity.sh


