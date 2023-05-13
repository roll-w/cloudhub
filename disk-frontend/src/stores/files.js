import {defineStore} from "pinia";

export const useFileStore = defineStore('file', {
    state: () => ({
        uploads: [],
        showUploadDialog: false,
    }),
    getters: {
        getUploads: state => state.uploads,

        isUploadDialog: state => state.showUploadDialog,
    },
    actions: {
        /**
         * @param uploads {Array}
         */
        setUploads(uploads) {
            this.uploads = uploads
        },
        /**
         * @param upload {Object}
         */
        updateUpload(upload) {
            const index = this.uploads.findIndex(item => item.id === upload.id)
            if (index !== -1) {
                this.uploads[index] = upload
                return
            }
            this.uploads.push(upload)

        },
        removeUpload(upload) {
            const index = this.uploads.findIndex(item => item.id === upload.id)
            if (index !== -1) {
                this.uploads.splice(index, 1)
            }
        },
        findUpload(id) {
            return this.uploads.find(item => item.id === id)
        },

        showTransferDialog() {
            this.showUploadDialog = true
        },

        hideTransferDialog() {
            this.showUploadDialog = false
        }
    }
});

export class UploadFile {

}
