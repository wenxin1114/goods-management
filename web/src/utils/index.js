import { messageType } from '../enum';
export default {
    computed: {
    },
    methods: {
        // 发送通知弹窗
        msgAlert(message, variant = messageType.Info) {
            if (!Object.values(messageType).includes(variant)) {
                variant = messageType.Info;
            }
            this.$message({
                showClose: true,
                message: message,
                type: variant
              });
        }
    }
}