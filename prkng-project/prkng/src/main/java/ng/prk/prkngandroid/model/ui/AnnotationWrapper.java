package ng.prk.prkngandroid.model.ui;


public class AnnotationWrapper {
    protected String featureId;
    protected String title;
    protected int type;

    public AnnotationWrapper(String featureId, String title, int type) {
        this.featureId = featureId;
        this.title = title;
        this.type = type;
    }

    private AnnotationWrapper(Builder builder) {
        this.featureId = builder.featureId;
        this.title = builder.title;
        this.type = builder.type;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Builder {
        protected String featureId;
        protected String title;
        protected int type;

        public Builder(String featureId) {
            this.featureId = featureId;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public AnnotationWrapper build() {
            return new AnnotationWrapper(this);
        }
    }
}
