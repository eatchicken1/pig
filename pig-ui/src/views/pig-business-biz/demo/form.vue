<template>
  <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
    :close-on-click-modal="false" draggable>
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
      <el-row :gutter="24">
        <el-col :span="24" class="mb20">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名"/>
          </el-form-item>
        </el-col>
        <el-col :span="24" class="mb20">
          <el-form-item label="昵称" prop="nicename">
            <el-input v-model="form.nicename" placeholder="请输入昵称"/>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="DemoDialog">
// ========== 1. 导入语句 ==========
import { useDict } from '/@/hooks/dict';
import { rule } from '/@/utils/validate';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/pig-business-biz/demo';

// ========== 2. 组件定义 ==========
// 定义组件事件
const emit = defineEmits(['refresh']);

// ========== 3. 响应式数据定义 ==========
// 基础响应式变量
const dataFormRef = ref(); // 表单引用
const visible = ref(false); // 弹窗显示状态
const loading = ref(false); // 加载状态

// 表单数据对象
const form = reactive({
  id: '', // 主键
  username: '', // 用户名
  nicename: '', // 昵称
});

// ========== 4. 字典数据处理 ==========

// ========== 5. 表单校验规则 ==========
const dataRules = ref({
});

// ========== 6. 方法定义 ==========
// 获取详情数据
const getDemoData = async (id: string) => {
  try {
    loading.value = true;
    const { data } = await getObj({ id: id });
    // 直接将第一条数据赋值给表单
    Object.assign(form, data[0]);
  } catch (error) {
    useMessage().error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 打开弹窗方法
const openDialog = (id: string) => {
  visible.value = true;
  form.id = '';

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  // 获取Demo信息
  if (id) {
    form.id = id;
    getDemoData(id);
  }
};

// 提交表单方法
const onSubmit = async () => {
  loading.value = true; // 防止重复提交
  
  // 表单校验
  const valid = await dataFormRef.value.validate().catch(() => {});
  if (!valid) {
    loading.value = false;
    return false;
  }

  try {
    // 根据是否有ID判断是新增还是修改
    form.id ? await putObj(form) : await addObj(form);
    useMessage().success(form.id ? '修改成功' : '添加成功');
    visible.value = false;
    emit('refresh'); // 通知父组件刷新列表
  } catch (err: any) {
    useMessage().error(err.msg);
  } finally {
    loading.value = false;
  }
};

// ========== 7. 对外暴露 ==========
// 暴露方法给父组件
defineExpose({
  openDialog
});
</script> 