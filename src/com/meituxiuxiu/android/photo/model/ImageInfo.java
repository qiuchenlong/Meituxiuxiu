package com.meituxiuxiu.android.photo.model;


/**
 * 图片实体类
 * @author qiuchenlong on 2016.03.25
 *
 */
public class ImageInfo {
	public String path;
    public String name;
    public long time;

    public ImageInfo(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageInfo other = (ImageInfo) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
