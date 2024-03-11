module org.ailuroedus {

    exports org.ailuroedus.service;
    exports org.ailuroedus.config;

    requires freemarker;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires lombok;
}
