package com.github.zachdeibert.jlinq.installer.conversions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zachdeibert.jlinq.installer.Arguments;

public class BootClasspathGenerator extends AbstractInjectingConverter {
	private static final Logger log = LogManager.getLogger();

	@Override
	public void convert(final Arguments args) {
		final File rt = new File(new File(args.javaDirectory, "lib"), "rt.jar");
		try ( final FileInputStream filein = new FileInputStream(rt)) {
			try ( final ZipInputStream zipin = new ZipInputStream(filein)) {
				for ( ZipEntry entry; (entry = zipin.getNextEntry()) != null; ) {
					final String filename = entry.getName();
					log.debug("Got file {}", filename);
					if ( classes.any(s -> filename.equals(s)) ) {
						log.info("Attempting to convert {}", filename);
						final File out = new File(args.outputDirectory, filename);
						out.getParentFile().mkdirs();
						try ( final FileOutputStream fileout = new FileOutputStream(out)) {
							convert(filename, zipin, fileout);
						}
					}
				}
			}
		} catch ( final IOException ex ) {
			log.catching(ex);
			return;
		}
		log.info("Conversion successful; changes written to disk");
	}
}
