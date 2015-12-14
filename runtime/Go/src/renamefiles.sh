for file in */*/*.go
do
    cat $file > dummy; $ echo cat dummy > $file
done

