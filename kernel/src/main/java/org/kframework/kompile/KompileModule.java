// Copyright (c) 2014-2015 K Team. All Rights Reserved.
package org.kframework.kompile;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.kframework.backend.Backend;
import org.kframework.backend.Backends;
import org.kframework.kil.loader.Context;
import org.kframework.main.FrontEnd;
import org.kframework.main.GlobalOptions;
import org.kframework.main.Tool;
import org.kframework.utils.errorsystem.KExceptionManager;
import org.kframework.utils.file.DefinitionDir;
import org.kframework.utils.file.KompiledDir;
import org.kframework.utils.file.TempDir;
import org.kframework.utils.file.WorkingDir;
import org.kframework.utils.inject.Options;
import org.kframework.utils.options.SMTOptions;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

public class KompileModule extends AbstractModule {

    private final Context context;
    private final KompileOptions options;

    public KompileModule(Context context, KompileOptions options) {
        this.context = context;
        this.options = options;
    }

    @Override
    protected void configure() {
        bind(FrontEnd.class).to(KompileFrontEnd.class);
        bind(Tool.class).toInstance(Tool.KOMPILE);
        bind(Context.class).toInstance(context);
        bind(KompileOptions.class).toInstance(options);
        bind(GlobalOptions.class).toInstance(options.global);
        bind(SMTOptions.class).toInstance(options.experimental.smt);

        Multibinder<Object> optionsBinder = Multibinder.newSetBinder(binder(), Object.class, Options.class);
        optionsBinder.addBinding().toInstance(options);
        Multibinder<Class<?>> experimentalOptionsBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<Class<?>>() {}, Options.class);
        experimentalOptionsBinder.addBinding().toInstance(KompileOptions.Experimental.class);
        experimentalOptionsBinder.addBinding().toInstance(SMTOptions.class);

        MapBinder<String, Backend> mapBinder = MapBinder.newMapBinder(
                binder(), String.class, Backend.class);
    }

    @Provides @DefinitionDir
    File definitionDir(@WorkingDir File workingDir, KompileOptions options) {
        if (options.directory == null) {
            return options.mainDefinitionFile().getParentFile();
        }
        File f = new File(options.directory);
        if (f.isAbsolute()) return f;
        return new File(workingDir, options.directory);
    }

    @Provides @KompiledDir
    File kompiledDir(@DefinitionDir File defDir, KompileOptions options, Backend backend, @TempDir File tempDir) {
        if (!backend.generatesDefinition()) {
            return tempDir;
        }
        return new File(defDir, FilenameUtils.removeExtension(options.mainDefinitionFile().getName()) + "-kompiled");
    }

    @Provides @Backend.Autoinclude
    boolean autoinclude(Backend backend) {
        return backend.autoinclude();
    }

    @Provides @Backend.AutoincludedFile
    String autoincludedFile(Backend backend) {
        return backend.autoincludedFile();
    }

    @Provides
    Backend getBackend(KompileOptions options, Map<String, Backend> map, KExceptionManager kem) {
        Backend backend = map.get(options.backend);
        if (backend == null) {
            throw KExceptionManager.criticalError("Invalid backend: " + options.backend
                    + ". It should be one of " + map.keySet());
        }
        return backend;
    }
}
