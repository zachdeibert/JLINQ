package com.github.zachdeibert.jlinq.installer.conversions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zachdeibert.jlinq.installer.Arguments;

public class JdkConverter extends AbstractInjectingConverter {
	private static final Logger log = LogManager.getLogger();

	@Override
	public void convert(final Arguments args) {
		log.info("Attempting to convert rt.jar");
		final File rt = new File(new File(args.javaDirectory, "lib"), "rt.jar");
		final File backup = new File(new File(args.javaDirectory, "lib"), "rt.jar.bk");
		final File tmp = new File(new File(args.outputDirectory, "lib"), "rt.jar.new");
		final File out = new File(new File(args.outputDirectory, "lib"), "rt.jar");
		tmp.getParentFile().mkdirs();
		try ( final FileInputStream filein = new FileInputStream(rt)) {
			try ( final ZipInputStream zipin = new ZipInputStream(filein)) {
				try ( final FileOutputStream fileout = new FileOutputStream(tmp)) {
					try ( final ZipOutputStream zipout = new ZipOutputStream(fileout)) {
						final byte[] buffer = new byte[4096];
						for ( ZipEntry entry; (entry = zipin.getNextEntry()) != null; ) {
							final String filename = entry.getName();
							log.debug("Got file {}", filename);
							if ( classes.any(s -> filename.equals(s)) ) {
								log.trace("Converting file {}", filename);
								zipout.putNextEntry(new ZipEntry(filename));
								convert(filename, zipin, zipout);
							} else {
								log.trace("Copying file {} verbatim", filename);
								zipout.putNextEntry(entry);
								for ( int len; (len = zipin.read(buffer)) > 0; zipout.write(buffer, 0, len) )
									;
							}
						}
					}
				}
			}
		} catch ( final IOException ex ) {
			log.catching(ex);
			return;
		}
		log.info("Conversion successful; writing changes to disk");
		if ( args.outputDirectory == args.javaDirectory ) {
			rt.renameTo(backup);
		}
		tmp.renameTo(out);
		if ( args.outputDirectory == args.javaDirectory ) {
			backup.delete();
		}
		log.info("All changes written");
	}
}
