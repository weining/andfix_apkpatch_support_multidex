package com.euler.patch.diff;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.dexbacked.util.FixedSizeSet;

public class DexDiffer {
    public DiffInfo diff(File newFile, File oldFile) throws IOException {
//        DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(newFile, "classes.dex", 19, true);
//        DexBackedDexFile newDexFile2 = DexFileFactory.loadDexFile(newFile, "classes2.dex", 19, true);
//        DexBackedDexFile oldDexFile = DexFileFactory.loadDexFile(oldFile, "classes.dex", 19, true);
//        DexBackedDexFile oldDexFile2 = DexFileFactory.loadDexFile(oldFile, "classes2.dex", 19, true);
//
//        FixedSizeSet<DexBackedClassDef> newclasses = (FixedSizeSet) newDexFile.getClasses();
//        FixedSizeSet<DexBackedClassDef> newclasses2 = (FixedSizeSet) newDexFile2.getClasses();
//        HashSet<DexBackedClassDef> newset = new HashSet<DexBackedClassDef>();
//        mergeHashSet(newset, newclasses);
//        mergeHashSet(newset, newclasses2);
//
//        FixedSizeSet<DexBackedClassDef> oldclasses = (FixedSizeSet) oldDexFile.getClasses();
//        FixedSizeSet<DexBackedClassDef> oldclasses2 = (FixedSizeSet) oldDexFile2.getClasses();
//        HashSet<DexBackedClassDef> oldset = new HashSet<DexBackedClassDef>();
//        mergeHashSet(oldset, oldclasses);
//        mergeHashSet(oldset, oldclasses2);
    	HashSet<DexBackedClassDef> newset = getClassSet(newFile);
    	HashSet<DexBackedClassDef> oldset = getClassSet(oldFile);
        DiffInfo info = DiffInfo.getInstance();

        boolean contains = false;
        Iterator<DexBackedClassDef> iter = newset.iterator();
        while (iter.hasNext()) {
            DexBackedClassDef newClazz = iter.next();
            Iterator<DexBackedClassDef> iter2 = oldset.iterator();
            while (iter2.hasNext()) {
                DexBackedClassDef oldClazz = iter2.next();
                if (newClazz.equals(oldClazz)) {
                    compareField(newClazz, oldClazz, info);
                    compareMethod(newClazz, oldClazz, info);
                    contains = true;
                    break;
                }
                if (!contains) {
                    info.addAddedClasses(newClazz);
                }
            }
        }
        return info;
    }
    
    private HashSet<DexBackedClassDef> getClassSet(File apkFile) throws IOException{
    	ZipFile localZipFile = new ZipFile(apkFile);
    	Enumeration localEnumeration = localZipFile.entries();
    	HashSet<DexBackedClassDef> newset = new HashSet<DexBackedClassDef>();
    	while (localEnumeration.hasMoreElements()) {
			ZipEntry localZipEntry = (ZipEntry) localEnumeration.nextElement();
			if (localZipEntry.getName().endsWith(".dex")) {
				DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(apkFile, localZipEntry.getName(), 19, true);
				FixedSizeSet<DexBackedClassDef> newclasses = (FixedSizeSet) newDexFile.getClasses();
				mergeHashSet(newset, newclasses);
			}
		}
    	return newset;
    }

    private void mergeHashSet(HashSet<DexBackedClassDef> set, FixedSizeSet<DexBackedClassDef> fset) {
        Iterator<DexBackedClassDef> tmpIter = fset.iterator();
        while (tmpIter.hasNext()) {
            DexBackedClassDef item = tmpIter.next();
            set.add(item);
        }
    }

    public void compareMethod(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
        compareMethod(newClazz.getMethods(), oldClazz.getMethods(), info);
    }

    public void compareMethod(Iterable<? extends DexBackedMethod> news, Iterable<? extends DexBackedMethod> olds,
            DiffInfo info) {
        for (DexBackedMethod reference : news)
            if (!reference.getName().equals("<clinit>")) {
                compareMethod(reference, olds, info);
            }
    }

    public void compareMethod(DexBackedMethod object, Iterable<? extends DexBackedMethod> olds, DiffInfo info) {
        for (DexBackedMethod reference : olds) {
            if (reference.equals(object)) {
                if ((reference.getImplementation() == null) && (object.getImplementation() != null)) {
                    info.addModifiedMethods(object);
                    return;
                }
                if ((reference.getImplementation() != null) && (object.getImplementation() == null)) {
                    info.addModifiedMethods(object);
                    return;
                }
                if ((reference.getImplementation() == null) && (object.getImplementation() == null)) {
                    return;
                }

                if (!reference.getImplementation().equals(object.getImplementation())) {
                    info.addModifiedMethods(object);
                    return;
                }
                return;
            }
        }

        info.addAddedMethods(object);
    }

    public void compareField(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
        compareField(newClazz.getFields(), oldClazz.getFields(), info);
    }

    public void compareField(Iterable<? extends DexBackedField> news, Iterable<? extends DexBackedField> olds,
            DiffInfo info) {
        for (DexBackedField reference : news)
            compareField(reference, olds, info);
    }

    public void compareField(DexBackedField object, Iterable<? extends DexBackedField> olds, DiffInfo info) {
        for (DexBackedField reference : olds) {
            if (reference.equals(object)) {
                if ((reference.getInitialValue() == null) && (object.getInitialValue() != null)) {
                    info.addModifiedFields(object);
                    return;
                }
                if ((reference.getInitialValue() != null) && (object.getInitialValue() == null)) {
                    info.addModifiedFields(object);
                    return;
                }
                if ((reference.getInitialValue() == null) && (object.getInitialValue() == null)) {
                    return;
                }
                if (reference.getInitialValue().compareTo(object.getInitialValue()) != 0) {
                    info.addModifiedFields(object);
                    return;
                }
                return;
            }
        }

        info.addAddedFields(object);
    }
}
