package org.modularsoft.PlayerHeadHunt.helpers;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class YamlFileManager {
    private final File file;
    private final Yaml yaml;

    @Getter
    @Setter
    private Map<String, Object> data;

    public YamlFileManager(File file) {
        this.file = file;

        // Configure YAML dump options
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        // Configure YAML representer
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        // Configure YAML loader options
        LoaderOptions loaderOptions = new LoaderOptions();

        // Initialize YAML parser
        this.yaml = new Yaml(new SafeConstructor(loaderOptions), representer, dumperOptions);

        // Load the YAML file contents
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{}");
                }
            }

            try (FileInputStream inputStream = new FileInputStream(file)) {
                data = yaml.load(inputStream);
                if (data == null) {
                    data = new java.util.LinkedHashMap<>();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            data = new java.util.LinkedHashMap<>();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}