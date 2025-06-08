FROM openjdk:17-slim

RUN apt-get update && \
    apt-get install -y \
        libfreetype6 \
        libfontconfig1 \
        xvfb \
        libxrender1 \
        libxtst6 \
        libxi6 && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY src /app/src

RUN javac src/generic/AudioBookPlayer.java

COPY start_xvfb.sh /start_xvfb.sh
RUN chmod +x /start_xvfb.sh

CMD ["/start_xvfb.sh"] 
# in .sh we have 'CMD ["java", "-cp", "src", "generic.AudioBookPlayer"]''


# "java"	Calls the Java Runtime (java) to run a Java application.
# "-cp"	Short for class path — it tells Java where to find your .class files.
# "src"	The folder containing your generic/AudioBookPlayer.class file — essentially the root of your package structure.
# "generic.AudioBookPlayer"	The fully qualified name of your main class (with package generic;), without .class extension.