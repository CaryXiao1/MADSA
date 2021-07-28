# What is MADSA?
'MADSA' or 'Musical Accuracy Development using Spectral Analysis' is a Java-based application designed to acompany the practices of musicians, increasing their rate of improvement with the pieces they are currently practicing. It qualified for and was presented at the 2019 Intel ISEF, where it won the Oracle Academy Award. Finally, it was published in the 2020 ACM Southeast Conference. (https://doi.org/10.1145/3374135.3385273)

# How does it work?
MADSA uses Fast Fourier Transforms (from JorenSix's TarsosDSP library) to directly compare the pitch, rhythm, and timing of a musician's performance to a reference sound file. By both converting and overlaying each source as spectrograms, differences between the two files are highlighted to the user. Other statistics (relative dynamics, number of lines drawn, etc.) are then calculated from each spectrogram and shown to the user. 
