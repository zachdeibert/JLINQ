package com.github.zachdeibert.jlinq.installer;

import java.awt.EventQueue;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zachdeibert.jlinq.installer.conversions.IConverter;
import com.github.zachdeibert.jlinq.installer.ui.ArgumentDialog;

import jlinq.IEnumerable;
import jlinq.Jlinq;
import jlinq.Pointer;
import jlinq.lambdas.Func1;

public abstract class Main {
	private static final Logger log = LogManager.getLogger();

	private static Arguments parse(final IEnumerable<String> args) {
		final Arguments a = new Arguments();
		final Pointer<Func1<String, Boolean>> override = new Pointer<Func1<String, Boolean>>();
		override.resolve = null;
		a.showHelp = !args.all(s -> {
			if ( override.resolve == null ) {
				switch ( s ) {
				case "-h":
				case "--help":
					return false;
				case "-o":
				case "--output":
					override.resolve = dir -> {
						final File f = new File(dir);
						if ( f.exists() ) {
							a.outputDirectory = f;
							return true;
						} else {
							log.error("Output directory must exist");
							return false;
						}
					};
					return true;
				case "-j":
				case "--jvm":
				case "--java":
					override.resolve = dir -> {
						final File f = new File(dir);
						if ( f.exists() && new File(new File(f, "lib"), "rt.jar").exists() ) {
							a.javaDirectory = f;
							return true;
						} else {
							log.error("Invalid java directory.");
							return false;
						}
					};
					return true;
				case "-a":
				case "--jar":
					override.resolve = path -> {
						final File f = new File(path);
						if ( f.exists() ) {
							a.jar = f;
							return true;
						} else {
							log.error("Jar file must exist");
							return false;
						}
					};
					return true;
				case "-t":
				case "--type":
					override.resolve = flag -> {
						try {
							a.type = ConversionType.valueOf(flag);
						} catch ( final IllegalArgumentException ex ) {
							log.error("Invalid conversion type");
							return false;
						}
						return true;
					};
					return true;
				default:
					log.error("Unknown option '{}'", s);
					return false;
				}
			} else {
				final Func1<String, Boolean> func = override.resolve;
				override.resolve = null;
				return func.run(s);
			}
		}) || override.resolve != null;
		return a;
	}

	private static void showHelp() {
		log.info("Usage: java -jar JLINQ-install.jar [options]");
		log.info("");
		log.info("Options:");
		log.info("    -h, --help         Shows this help message");
		log.info("    -o, --output       Sets the output directory for the generated files");
		log.info("    -j, --jvm, --java  Sets the directory to get the JVM files from");
		log.info("    -a, -jar           Sets the input jar for conversions");
		log.info("    -t, --type         Sets the type of conversion to do");
		log.info("");
		log.info("Conversion Types:");
		log.info("    JdkInstall         Installs JLINQ into your JVM so you can compile and run");
		log.info("                       applications");
		log.info("");
		log.info("    BootGeneration     Generates a boot classpath to use instead of globally");
		log.info("                       installing JLINQ");
		log.info("");
		log.info("    JarInlining        Inlines all JLINQ calls in a jar so it does not have to");
		log.info("                       be installed into the JVM to run");
	}

	private static void start(final Arguments args) {
		final IConverter converter = args.type.construct();
		if ( converter == null ) {
			log.fatal("Conversion type {} is not implemented", args.type);
		} else {
			converter.convert(args);
		}
	}

	public static void main(final String[] args) {
		log.info("Starting JLINQ installer");
		if ( args.length == 0 ) {
			log.info("Unable to find command line arguments, try `-h'");
			EventQueue.invokeLater(() -> {
				final ArgumentDialog dialog = new ArgumentDialog();
				dialog.addArgumentSavedListener(e -> {
					start(e.getArgs());
					System.exit(0);
				});
				dialog.setVisible(true);
			});
		} else {
			final Arguments a = parse(Jlinq.from(args));
			if ( a.showHelp ) {
				showHelp();
			} else {
				start(a);
			}
		}
	}
}
