package servlet;

import common.VerifyImage;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 创建磁盘工厂 缓冲区和磁盘目录
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 创建ServletFileUpload
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("utf-8");// 设置编码格式
        try {
            String fileName = null;
            String name = null;
            String newName = null;

            Integer type = 0;

            List<FileItem> files = upload.parseRequest(request);
            for (FileItem fileItem : files) {
                // 判断当前的数据时文件还是普通的表单
                // 是表单
                if (fileItem.isFormField()) {
                    name = fileItem.getFieldName();// 获取属性的名字
                    if ("type".equals(name)) // 获取属性的值
                        type = Integer.parseInt(fileItem.getString("utf-8"));
                } else {
                    // 是文件

                    // 获取文件上传的文件名
                    name = fileItem.getName();

                    // 以当前的总秒数来命名防止图片名称相同而覆盖
                    fileName = System.currentTimeMillis() + name;

                    // 定义一个新的文件来接收
                    newName = request.getServletContext()
                            .getRealPath("/upload/images/") + fileName;

                    File file = new File(newName);

                    // 从缓冲区写入磁盘
                    fileItem.write(file);
                    fileItem.delete();
                }
            }

            JSONObject object = VerifyImage.testImage(newName, type);
            object.put("image_src", "/upload/images/" + fileName);

            //返回json字符串
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write(object.toString());
        } catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        doPost(request, response);
    }
}
