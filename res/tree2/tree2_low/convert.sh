"ls"
for img in *.png; do
	ffmpeg -y -i $img -s 256x256 $img
done

